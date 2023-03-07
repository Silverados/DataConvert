package com.wyw.output;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter.Feature;
import com.wyw.serialize.ConvertCell;
import com.wyw.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class JsonOutputFormat extends OutputFormat {

    @Override
    public void output() throws IOException {
        outputFile = Path.of(context.convertConfig.outputRootPath, inputDO.outputFile.outputDir, exportFileName + "." + formatEnum.fileExtension);
        attrDefine();
        output(outputFile, genBody());
    }

    private String genBody() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("comment", StringUtils.replaceTemplate("Version: {${VERSION}}, Auto convert from file_name=${INPUT_FILE_NAME}, sheet_name=${INPUT_SHEET_NAME}", attrDefine));

        Map<Object, Map<String, Object>> map = new HashMap<>();
        for (var i = 0; i < rows.size(); i++) {
            Map<String, Object> temp = new HashMap<>();
            var primaryKey = getCell(i, 0);
            var primaryType = titles.get(0).fieldDefineDataList.get(index).fieldType;
            for (int j = 1; j < titles.size(); j++) {
                var title = titles.get(j);
                var field = title.fieldDefineDataList.get(index);
                var name = field.fieldName;
                var value = getLanguageTypeValue(field.fieldType, getCell(i, title.columnIndex).val, false);
                if (value == null) {
                    continue;
                }
                temp.put(name, value);
            }

            map.put(getLanguageTypeValue(primaryType, primaryKey.val, false), temp);
        }

        jsonMap.put(exportFileName, map);
        return JSON.toJSONString(jsonMap, Feature.PrettyFormat, Feature.WriteNonStringKeyAsString);
    }

    @Override
    protected String getLanguageTypeString(String type) {
        var lowerFirstCharacterType = StringUtils.lowerFirstCharacter(type);
        if ("str".equals(lowerFirstCharacterType) || "string".equals(lowerFirstCharacterType)) {
            return "String";
        } else if (lowerFirstCharacterType.startsWith("list") || lowerFirstCharacterType.startsWith("map")) {
            return StringUtils.upperFirstCharacter(type);
        } else if (lowerFirstCharacterType.startsWith("dict")) {
            return lowerFirstCharacterType.replace("dict", "Map");
        }
        return type;
    }

    private Object getLanguageTypeValue(String type, String value, boolean quote) {
        value = value.replaceAll("\\n", "");
        var languageType = getLanguageType(type);
        if (value.isEmpty()) {
            return null;
        }

        if (languageType.startsWith("List")) {
            var genericType = getFromGeneric(languageType);
            value = value.replace(" ", "");
            var valueArr = value.split(",");
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < valueArr.length; i++) {
                sb.append(getLanguageTypeValue(genericType, valueArr[i], true));
                if (i != valueArr.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return JSON.parseArray(sb.toString(), JSONReader.Feature.AllowUnQuotedFieldNames, JSONReader.Feature.SupportAutoType);
        }

        if (languageType.startsWith("Map")) {
            StringBuilder sb = new StringBuilder();
            sb.append("{").append(value).append("}");
            return JSON.parse(sb.toString(), JSONReader.Feature.AllowUnQuotedFieldNames);
        }


        return switch (languageType) {
            case "int", "Integer" -> (int)Double.parseDouble(value);
            case "long", "Long" -> (long)Double.parseDouble(value);
            case "float", "Float" -> (float)Double.parseDouble(value);
            case "String" -> quote ? '"' + value + '"' : value;
            default -> value;
        };

    }

    private String getFromGeneric(String type) {
        type = type.replace(" ", "");
        Matcher matcher = GENERIC_PATTERN.matcher(type);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private ConvertCell getCell(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).cells.get(columnIndex);
    }

    @Override
    public String setTemplate() {
        return """
                {
                    "comment": "Version: {${VERSION}}, Auto convert from file_name=${INPUT_FILE_NAME}, sheet_name=${INPUT_SHEET_NAME}",
                    "${CLASS_NAME}": {
                ${MAIN_BODY}
                    }
                }
                """;
    }


    static class JsonFormat {
        public Object key;
        public Map<String, Object> value;
    }
}
