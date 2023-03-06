package com.wyw.input;

import com.wyw.serialize.*;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * -----------------------------------------------------
 * 0: | HEAD |
 * 1: | ServerTypeDesc | ClientTypeDesc |
 * 2: | ServerType | ClientType |
 * 3: | ---------------------------------------------------
 * 4: | BODY |
 * 5: | DESC1 | DESC2 | DESC3
 * 6: | ServerField1 | ServerField2 | ServerFiled3 |
 * 7: | ClientField1 | ClientField2 | ClientField3 |
 * 8: | Data1 | Data2 | Data3 |
 * 9: | .....
 * ------------------------------------------------------
 */
public abstract class ExcelInputParser extends InputParser {

    private static final int HEAD_ROW = 2;
    private static final String BODY = "BODY";

    protected Workbook workbook;
    private DataFormatter dataFormatter;
    private FormulaEvaluator evaluator;

    /**
     * 描述存在 而客户端/服务端类型不存在  那这一列就是备注列
     * 客户端/服务端类型存在 描述不存在  合法但是不合理
     * @return 返回合法的最大列号
     */
    private static short getValidCellNum(List<Row> rows) {
        return rows.stream().map(Row::getLastCellNum).max(Comparator.comparingInt(a -> a)).orElseGet(() -> {return (short) 0;});
    }

    public abstract void initWorkbook() throws IOException;

    @Override
    public void init(File inputFile, Tag tag) {
        super.init(inputFile, tag);
        try {
            initWorkbook();
            dataFormatter = new DataFormatter();
            evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InputDO> parser() {
        var inputDoList = new ArrayList<InputDO>();
        tag.outputFiles.forEach(outputFile -> {
            var sheet = workbook.getSheet(outputFile.sheetName);
            // head不允许其他形式
            InputHead head = new InputHead();
            var formatSize = context.convertConfig.outputFormats.size();
            for (var i = 0; i < formatSize; i++) {
                head.add(sheet.getRow(HEAD_ROW).getCell(i).getStringCellValue());
            }

            // body允许中间空不止一行 所以行号要获取
            var bodyStartRowNum = getBodyStartRowNum(sheet);
            var rowIndex = bodyStartRowNum;
            var descRow = sheet.getRow(++rowIndex);

            var defineRowList = new ArrayList<Row>(formatSize);
            for (var i = 0; i < formatSize; i++) {
                defineRowList.add(sheet.getRow(++rowIndex));
            }

            var validCellNum = getValidCellNum(defineRowList);
            var titles = new ArrayList<BodyTitle>(validCellNum);
            for (var i = 0; i < validCellNum; i++) {
                var desc = descRow.getCell(i) != null ? descRow.getCell(i).getStringCellValue() : "";
                var fieldDefineDataList = new ArrayList<FieldDefineData>(defineRowList.size());
                var valid = false;
                for(var defineRow: defineRowList) {
                    var temp = getFileDefineData(defineRow.getCell(i));
                    valid |= (temp != null);
                    fieldDefineDataList.add(temp);
                }
                if (!valid) {
                    continue;
                }

                var bodyTitle = new BodyTitle(i, desc, fieldDefineDataList);
                titles.add(bodyTitle);
            }

            var maxRow = sheet.getLastRowNum();
            var rows = new ArrayList<RowData>(maxRow - bodyStartRowNum + 1);
            for (++rowIndex; rowIndex <= maxRow; rowIndex++) {
                var breakFlag = false;
                if (sheet.getRow(rowIndex) == null) {
                    continue;
                }
                var cells = new ArrayList<ConvertCell>(titles.size());
                for (var i = 0; i < titles.size(); i++) {
                    var title = titles.get(i);
                    var cell = sheet.getRow(rowIndex).getCell(title.columnIndex);
                    if (i == 0 && cell == null) {
                        breakFlag = true;
                        break;
                    }
                    cells.add(new ConvertCell(rowIndex, title.columnIndex, cell, getStringCellValue(cell)));
                }
                if (breakFlag) {
                    continue;
                }
                rows.add(new RowData(rowIndex, cells));
            }

            InputBody inputBody = new InputBody(titles, rows);
            InputDO inputDO = new InputDO(head, inputBody, tag, outputFile);
            inputDoList.add(inputDO);
        });

        return inputDoList;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.FORMULA) {
            switch (cell.getCachedFormulaResultType()) {
                case STRING -> {
                    return cell.getRichStringCellValue().getString();
                }
                case NUMERIC -> {
                    return String.valueOf(cell.getNumericCellValue());
                }
            }
        }
        return dataFormatter.formatCellValue(cell, evaluator);
    }

    private int getBodyStartRowNum(Sheet sheet) {
        for (var i = HEAD_ROW + 1; i <= sheet.getLastRowNum(); i++) {
            if (BODY.equals(sheet.getRow(i).getCell(0).getStringCellValue().toUpperCase().trim())) {
                return i;
            }
        }

        throw new RuntimeException("Can not find BODY row!");
    }

    private FieldDefineData getFileDefineData(Cell cell) {
        if (cell == null) {
            return null;
        }
        var nameAndType = cell.getStringCellValue().replace(" ", "").split(":");
        if (nameAndType.length != 2) {
            return null;
        }
        return new FieldDefineData(nameAndType[0], nameAndType[1]);
    }
}
