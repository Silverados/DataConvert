package com.wyw.output;

import com.wyw.serialize.BodyTitle;
import com.wyw.serialize.ConvertCell;
import com.wyw.util.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSharpOutputFormat extends OutputFormat {
    public static final Pattern pattern = Pattern.compile("\\w+<(.*)>");
    public static final Pattern digit = Pattern.compile(".*(\\d+).*");

    @Override
    public void output() throws IOException {
        super.output();
    }

    @Override
    public void attrDefine() {
        super.attrDefine();
        attrDefine.put("PRIMARY_KEY_TYPE", getPrimaryKeyType());
        attrDefine.put("FIELD_DECLARE", genFieldsDeclare());
        attrDefine.put("CODE_SEGMENT_LIMIT", String.valueOf(getCodeSegmentLimit()));
        attrDefine.put("INIT_METHODS_INVOKE", genInitMethodsInvoke());
        attrDefine.put("INIT_METHODS", genInitMethods());
    }

    private String genInitMethods() {
        StringBuilder sb = new StringBuilder();
        var rowIndex = 0;
        for (int i = 0; i < getCodeSegmentLimit(); i++) {
            var template = String.format("        private static void initData%d(Dictionary<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}> datas){\n", i);
            template = StringUtils.replaceTemplate(template, attrDefine);
            sb.append(template);
            var endIndex = Math.min(rowIndex + getMethodRowLimit() / titles.size(), rows.size());
            sb.append(initData(rowIndex, endIndex));
            sb.append("        }\n");
            if (i != getCodeSegmentLimit() - 1) {
                sb.append("\n");
            }

            rowIndex = endIndex;
        }
        return sb.toString();
    }

    private String initData(int beginRowIndex, int endRowIndex) {
        StringBuilder sb = new StringBuilder();
        for (; beginRowIndex < endRowIndex; beginRowIndex++) {
            var primaryKey = getCell(beginRowIndex, titles.get(0).columnIndex);
            var type = titles.get(0).fieldDefineDataList.get(index).fieldType;
            sb.append(String.format("            datas.Add(%s, new %s() {\n", getLanguageTypeValue(type, primaryKey.val), exportFileName));
            for (int i = 1; i < titles.size(); i++) {
                var title = titles.get(i);
                var field = title.fieldDefineDataList.get(index);
                var value = getLanguageTypeValue(field.fieldType, getCell(beginRowIndex, title.columnIndex).val);
                if (value.isEmpty()) {
                    continue;
                }
                sb.append(String.format("                %s = %s,\n", field.fieldName, value));
            }
            sb.append("            });\n");
        }
        return sb.toString();
    }

    private ConvertCell getCell(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).cells.get(columnIndex);
    }

    private String genInitMethodsInvoke() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getCodeSegmentLimit(); i++) {
            sb.append(String.format("                initData%d(datas);", i));
            if (i != getCodeSegmentLimit() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private int getCodeSegmentLimit() {
        return rows.size() * titles.size() / getMethodRowLimit() + 1;
    }

    private String genFieldsDeclare() {
        StringBuilder sb = new StringBuilder();
        for (var title : titles) {
            genFieldDeclare(sb, title);
        }
        return sb.toString();
    }

    private void genFieldDeclare(StringBuilder sb, BodyTitle title) {
        var desc = title.desc;
        var columnIndex = title.columnIndex;
        var nameAndType = title.fieldDefineDataList.get(index);
        var fieldName = nameAndType.fieldName;
        var fieldType = nameAndType.fieldType;
        if (!desc.isEmpty()) {
            sb.append(String.format("        //%s\n", title.desc));
        }
        sb.append(String.format("        private %s %s {get;set;}\n", getLanguageType(fieldType), fieldName));
    }

    @Override
    protected String getLanguageTypeString(String type) {
        var lowerFirstCharacterType = StringUtils.lowerFirstCharacter(type);
        if ("str".equals(lowerFirstCharacterType) || "string".equals(lowerFirstCharacterType)) {
            return "string";
        } else if ("Integer".equals(type)) {
            return "int";
        } else if ("long".equals(lowerFirstCharacterType)) {
            return "long";
        } else if (lowerFirstCharacterType.startsWith("void")) {
            return parseLambda(lowerFirstCharacterType, "Action");
        } else if (lowerFirstCharacterType.startsWith("func")) {
            return parseLambda(lowerFirstCharacterType, "Func");
        } else if (lowerFirstCharacterType.startsWith("list")) {
            var sb = new StringBuilder("List<");
            var genericType = getFromGeneric(lowerFirstCharacterType);
            sb.append(getLanguageType(genericType));
            sb.append(">");
            return sb.toString();
        } else if (lowerFirstCharacterType.startsWith("dict") || lowerFirstCharacterType.startsWith("map")) {
            var sb = new StringBuilder("Dictionary<");
            var genericType = getFromGeneric(lowerFirstCharacterType);
            var typeArr = genericType.split(",");
            sb.append(getLanguageType(typeArr[0])).append(", ").append(getLanguageType(typeArr[1]));
            sb.append(">");
            return sb.toString();
        }
        return type;
    }

    private String parseLambda(String lowerFirstCharacterType, String name) {
        var genericType = getFromGeneric(lowerFirstCharacterType);
        if (genericType.isEmpty()) {
            return name;
        }
        var typeArr = StringUtils.splitString(genericType);
        var sb = new StringBuilder(name).append("<");
        for (int i = 0; i < typeArr.size(); i++) {
            sb.append(getLanguageType(typeArr.get(i)));
            if (i != typeArr.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(">");

        return sb.toString();
    }

    private String getLanguageTypeValue(String type, String value) {
        value = value.replaceAll("\\n", "");
        var languageType = getLanguageType(type);
        if (value.isEmpty()) {
            return value;
        }

        if (languageType.startsWith("Action") || languageType.startsWith("Func")) {
            StringBuilder sb = new StringBuilder();
            var matcher = digit.matcher(type);
            var count = 0;
            if (matcher.find()) {
                count = Integer.parseInt(matcher.group(1));
            }
            sb.append('(');
            for (int i = 0; i < count; i++) {
                sb.append("arg").append(i);
                if (i != count - 1) {
                    sb.append(" ,");
                }
            }
            sb.append(") => { ").append(value).append(" }");
            return sb.toString();
        }

        if (languageType.startsWith("List")) {
            var genericType = getFromGeneric(languageType);
            var valueArr = value.split(",");
            StringBuilder sb = new StringBuilder();
            sb.append("new List<%s>{".formatted(genericType));
            for (int i = 0; i < valueArr.length; i++) {
                sb.append(getLanguageTypeValue(genericType, valueArr[i]));
                if (i != valueArr.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("}");
            return sb.toString();
        }

        if (languageType.startsWith("Dictionary")) {
            var generic = getFromGeneric(languageType).split(",");
            var genericKeyType = generic[0];
            var genericValueType = generic[1];

            StringBuilder sb = new StringBuilder();
            var valueArr = value.split(",");
            sb.append("new Dictionary<%s, %s>(){".formatted(genericKeyType, genericValueType));
            for (int i = 0; i < valueArr.length; i++) {
                var valArr = valueArr[i].split(":");
                var key = valArr[0];
                var val = valArr[1];
                sb.append("{");
                sb.append(getLanguageTypeValue(genericKeyType, key));
                sb.append(",");
                sb.append(getLanguageTypeValue(genericValueType, val));
                sb.append("}");
                if (i != valueArr.length - 1) {
                    sb.append(", ");
                }
            }

            sb.append("}");
            return sb.toString();
        }

        return switch (languageType) {
            case "int", "Integer" -> String.valueOf((int) Double.parseDouble(value));
            case "string" -> '"' + value + '"';
            case "long", "Long" -> value + 'L';
            case "float", "Float" -> value + 'F';
            default -> value;
        };

    }

    private String getFromGeneric(String type) {
        type = type.replace(" ", "");
        Matcher matcher = pattern.matcher(type);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private String getPrimaryKeyType() {
        var title = titles.get(0);
        return getLanguageType(title.fieldDefineDataList.get(index).fieldType);
    }

    @Override
    public String setTemplate() {
        return """
                // Version: {${VERSION}}, Auto convert from file_name=${INPUT_FILE_NAME}, sheet_name=${INPUT_SHEET_NAME}
                using System.Collections.Generic;
                                
                ${CUSTOM_BLOCK_START}
                ${CUSTOM_BLOCK_END}
                                
                namespace Config {
                                
                    public partial class ${CLASS_NAME} {
                                    
                ${FIELD_DECLARE}
                                    
                        public static Dictionary<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}> DATAS {get;set;}
                        
                        public static ${CLASS_NAME} get(${PRIMARY_KEY_TYPE} id) {
                            DATAS.TryGetValue(id, out var r);
                            return r;
                        }
                        
                ${CUSTOM_BLOCK_START2}
                        private static void preInit(Dictionary<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}> data) {
                        }
                        
                        private static void postInit() {
                        }
                ${CUSTOM_BLOCK_END2}
                        
                        static ${CLASS_NAME}() {
                            init();
                        }
                                    
                        private static void init() {
                            var data = new Dictionary<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}>(${ROWS});
                            preInit(data);
                            initDatas(data);
                            DATAS = data;
                            postInit();
                        }
                        
                        private static void initDatas(Dictionary<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}> datas) {
                            for(var i = 0; i < ${CODE_SEGMENT_LIMIT}; i++) {
                ${INIT_METHODS_INVOKE}
                            }
                        }
                        
                ${INIT_METHODS}
                    }
                }
                """;
    }
}
