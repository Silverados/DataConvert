package com.wyw.input;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class XlsxInputParser extends ExcelInputParser {

    @Override
    public void initWorkbook() throws IOException {
        workbook = new XSSFWorkbook(new FileInputStream(inputFile));
    }
}
