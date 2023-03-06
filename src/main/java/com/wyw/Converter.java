package com.wyw;

import com.wyw.input.InputParser;
import com.wyw.input.InputParserFactory;
import com.wyw.output.OutputFormat;
import com.wyw.output.OutputFormatFactory;
import com.wyw.serialize.InputDO;
import com.wyw.serialize.Tag;
import com.wyw.util.JsonUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 根据配置决定使用哪种解析器
 */
public class Converter {

    public AppContext context;

    public Converter() {
        context = AppContext.getInstance();
    }

    public static String getFileExtension(String url) {
        var index = url.lastIndexOf('.');
        assert 0 <= index;
        return url.substring(index + 1);
    }

    public void convert() {
        if (Constant.DEFAULT_TAG.equals(context.tag)) {
            convertAll();
            return;
        }

        var tag = JsonUtil.getObject(context.tags, context.tag, Tag.class);
        convert(tag);
    }

    public void convertAll() {
        context.tags.forEach((key, value) -> {
            convert(JsonUtil.getObject(context.tags, key, Tag.class));
        });

    }

    public void convert(Tag tag) {
        if (tag == null) {
            return;
        }

        Path inputPath = Path.of(context.convertConfig.inputRootPath, tag.inputFile);
        if (!Files.exists(inputPath)) {
            System.out.println("Path: " + inputPath + " not exist!");
            return;
        }

        File inputFile = inputPath.toFile();
        InputParser inputParser = InputParserFactory.getInputParser(getFileExtension(inputFile.getName()));
        inputParser.init(inputFile, tag);
        List<InputDO> inputDOS = inputParser.parser();

        for (var data : inputDOS) {
            List<String> outputFormats = context.convertConfig.outputFormats;
            for (int i = 0; i < outputFormats.size(); i++) {
                if (!context.isExport.get(i)) {
                    continue;
                }
                String format = outputFormats.get(i);
                output(data, format, i);
            }
        }
    }

    public void output(InputDO data, String formatName, int index) {
        OutputFormat outputFormat = OutputFormatFactory.getOutputFormat(formatName);
        outputFormat.init(data, index);
        try {
            outputFormat.output();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

}
