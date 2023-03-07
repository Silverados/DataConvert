package com.wyw.output;

public class OutputFormatFactory {

    public static OutputFormat getOutputFormat(String formatName) {
        OutputFormatEnum outputFormatEnum = OutputFormatEnum.valueOf(formatName);
        var outputFormat = outputFormatEnum.getNoArgConstructor().get();
        outputFormat.formatEnum = outputFormatEnum;
        return outputFormat;
    }

}
