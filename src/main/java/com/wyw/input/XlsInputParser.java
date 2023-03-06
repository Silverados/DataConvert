package com.wyw.input;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class XlsInputParser extends ExcelInputParser {

    @Override
    public void initWorkbook() throws IOException {
        workbook = new HSSFWorkbook(new FileInputStream(inputFile));
    }
}
