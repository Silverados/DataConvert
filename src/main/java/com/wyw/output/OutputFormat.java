package com.wyw.output;

import com.wyw.AppContext;
import com.wyw.serialize.BodyTitle;
import com.wyw.serialize.InputDO;
import com.wyw.serialize.RowData;
import com.wyw.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

public abstract class OutputFormat {

    public static final String CUSTOM_BLOCK_START = "//====================CUSTOM BLOCK START====================";
    public static final String CUSTOM_BLOCK_END = "//====================CUSTOM BLOCK END====================";
    public static final String CUSTOM_BLOCK_START2 = "//====================CUSTOM BLOCK 2 START====================";
    public static final String CUSTOM_BLOCK_END2 = "//====================CUSTOM BLOCK 2 END====================";
    public static final String CUSTOM_BLOCK_START3 = "//====================CUSTOM BLOCK 3 START====================";
    public static final String CUSTOM_BLOCK_END3 = "//====================CUSTOM BLOCK 3 END====================";

    public static final Pattern CUSTOM_BLOCK_PATTERN = Pattern.compile(CUSTOM_BLOCK_START + "([\\s\\S]*)" + CUSTOM_BLOCK_END, Pattern.UNICODE_CHARACTER_CLASS);
    public static final Pattern CUSTOM_BLOCK_PATTERN2 = Pattern.compile(CUSTOM_BLOCK_START2 + "([\\s\\S]*)" + CUSTOM_BLOCK_END2, Pattern.UNICODE_CHARACTER_CLASS);
    public static final Pattern CUSTOM_BLOCK_PATTERN3 = Pattern.compile(CUSTOM_BLOCK_START3 + "([\\s\\S]*)" + CUSTOM_BLOCK_END3, Pattern.UNICODE_CHARACTER_CLASS);

    protected final String TEMPLATE;
    public OutputFormatEnum type;
    // 标识是第几个导出的format
    public int index;
    public Map<String, String> attrDefine;
    public InputDO inputDO;

    protected AppContext context;
    protected Path outputFile;

    protected String exportFileName;
    protected List<BodyTitle> titles;
    protected List<RowData> rows;

    protected OutputFormat() {
        context = AppContext.getInstance();
        TEMPLATE = setTemplate();
        attrDefine = new HashMap<>(16);
    }

    public void output() throws IOException {
        outputFile = Path.of(context.convertConfig.outputRootPath, inputDO.outputFile.outputDir, exportFileName + "." + type.fileExtension);
        attrDefine();
        var outputString = StringUtils.replaceTemplate(TEMPLATE, attrDefine);
        output(outputFile, outputString);
    }

    public void output(Path outputFilePath, String outputString) {
        try {
            if (Files.exists(outputFilePath)) {
                if (context.mergeSource) {
                    outputString = StringUtils.mergeString(outputString, Files.readString(outputFilePath), getPatterns());
                }
            } else {
                Files.createDirectories(outputFilePath.getParent());
            }

            Files.writeString(outputFilePath, outputString, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void attrDefine() {
        attrDefine.put("CUSTOM_BLOCK_START", CUSTOM_BLOCK_START);
        attrDefine.put("CUSTOM_BLOCK_END", CUSTOM_BLOCK_END);
        attrDefine.put("CUSTOM_BLOCK_START2", CUSTOM_BLOCK_START2);
        attrDefine.put("CUSTOM_BLOCK_END2", CUSTOM_BLOCK_END2);
        attrDefine.put("CUSTOM_BLOCK_START3", CUSTOM_BLOCK_START3);
        attrDefine.put("CUSTOM_BLOCK_END3", CUSTOM_BLOCK_END3);

        attrDefine.put("VERSION", getVersion());
        attrDefine.put("CLASS_NAME", exportFileName);
        attrDefine.put("INPUT_FILE_NAME", inputDO.tag.inputFile);
        attrDefine.put("INPUT_SHEET_NAME", inputDO.outputFile.sheetName);
        attrDefine.put("ROWS", String.valueOf(inputDO.body.rows.size()));
    }

    private String getVersion() {
        return LocalDateTime.now().toString();
    }

    public Pattern[] getPatterns() {
        return new Pattern[]{CUSTOM_BLOCK_PATTERN, CUSTOM_BLOCK_PATTERN2, CUSTOM_BLOCK_PATTERN3};
    }

    protected String getLanguageType(String type) {
        return type;
    }

    public abstract String setTemplate();

    public void init(InputDO data, int index) {
        this.inputDO = data;
        this.index = index;
        exportFileName = inputDO.head.exportFileName.get(index);
        titles = new ArrayList<>(inputDO.body.titles.size());
        rows = inputDO.body.rows;
        titlesValid(inputDO.body.titles);
    }

    private void titlesValid(List<BodyTitle> rawTitles) {
        var set = new HashSet<String>();
        for (var title: rawTitles) {
            var field = title.fieldDefineDataList.get(index);
            if (field == null) {
                continue;
            }
            var fieldName = field.fieldName;
            if (set.contains(fieldName)) {
                throw new RuntimeException("Duplicate title name: " + fieldName + ", Column Index: " + title.columnIndex + ", Convert type: " + type);
            }
            set.add(fieldName);
            titles.add(title);
        }
    }


    /**
     * 方法的行数限制
     */
    public int getMethodRowLimit() {
        return 500;
    }

}
