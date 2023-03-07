package com.wyw.output;

import com.wyw.serialize.BodyTitle;
import com.wyw.serialize.ConvertCell;
import com.wyw.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaOutputFormat extends OutputFormat {
    public static final Pattern pattern = Pattern.compile("\\w+<(.*)>");
    private static boolean exportBaseConfig = true;

    @Override
    public void output() throws IOException {
        super.output();
        outputBaseConfig();
    }

    private void outputBaseConfig() {
        if (!exportBaseConfig) return;
        String template = """
                // Version: {${VERSION}}
                package com.wyw.config;
                                
                import java.util.*;
                                
                ${CUSTOM_BLOCK_START}
                ${CUSTOM_BLOCK_END}

                public class BaseConfig {
                                
                ${CUSTOM_BLOCK_START2}
                ${CUSTOM_BLOCK_END2}
                    
                    @FunctionalInterface
                    public interface Void0 {
                        void run();
                    }

                    @FunctionalInterface
                    public interface Void1<T> {
                        void run(T t);
                    }

                    @FunctionalInterface
                    public interface Void2<T, U> {
                        void run(T t, U u);
                    }

                    @FunctionalInterface
                    public interface Void3<T, U, V> {
                        void run(T t, U u, V v);
                    }

                    @FunctionalInterface
                    public interface Func0<R> {
                        R run();
                    }

                    @FunctionalInterface
                    public interface Func1<T, R> {
                        R run(T t);
                    }

                    @FunctionalInterface
                    public interface Func2<T, U, R> {
                        R run(T t, U u);
                    }

                    @FunctionalInterface
                    public interface Func3<T, U, V, R> {
                        R run(T t, U u, V v);
                    }
                }
                """;
        Path path = Path.of(context.convertConfig.outputRootPath, "BaseConfig.java");
        var outputString = StringUtils.replaceTemplate(template, attrDefine);
        output(path, outputString);
        exportBaseConfig = false;
    }

    @Override
    public void attrDefine() {
        super.attrDefine();
        attrDefine.put("PRIMARY_KEY_TYPE", getPrimaryKeyType());
        attrDefine.put("FIELD_DECLARE", genFieldsDeclare());
        attrDefine.put("CODE_SEGMENT_LIMIT", String.valueOf(getCodeSegmentLimit()));
        attrDefine.put("INIT_METHODS_INVOKE", genInitMethodsInvoke());
        attrDefine.put("INIT_METHODS", genInitMethods());
        attrDefine.put("FIELD_GETTER_SETTER", genGetterSetter());
    }

    private String genGetterSetter() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < titles.size(); i++) {
            BodyTitle title = titles.get(i);
            genFieldGetterSetter(sb, title);
            if (i != titles.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void genFieldGetterSetter(StringBuilder sb, BodyTitle title) {
        var nameAndType = title.fieldDefineDataList.get(index);
        var fieldName = nameAndType.fieldName;
        var fieldType = nameAndType.fieldType;
        var template = """
                    public void set${UPPER_FIELD_NAME}(${FIELD_TYPE} ${FIELD_NAME}) {
                        this.${FIELD_NAME} = ${FIELD_NAME};
                    }
                    
                    public ${FIELD_TYPE} get${UPPER_FIELD_NAME}() {
                        return ${FIELD_NAME};
                    }
                """;
        var dict = new HashMap<String, String>();
        dict.put("UPPER_FIELD_NAME", StringUtils.upperFirstCharacter(fieldName));
        dict.put("FIELD_NAME", fieldName);
        dict.put("FIELD_TYPE", getLanguageType(fieldType));
        sb.append(StringUtils.replaceTemplate(template, dict));
    }

    private String genInitMethods() {
        StringBuilder sb = new StringBuilder();
        var rowIndex = 0;
        for (int i = 0; i < getCodeSegmentLimit(); i++) {
            var template = String.format("    private static void initData%d(Map<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}> datas){\n", i);
            template = StringUtils.replaceTemplate(template, attrDefine);
            sb.append(template);
            var endIndex = Math.min(rowIndex + getMethodRowLimit() / titles.size(), rows.size());
            sb.append(initData(rowIndex, endIndex));
            sb.append("    }\n");
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
            sb.append(String.format("        datas.put(%s, new %s() {{\n", getLanguageTypeValue(type, primaryKey.val), exportFileName));
            for (int i = 1; i < titles.size(); i++) {
                var title = titles.get(i);
                var field = title.fieldDefineDataList.get(index);
                var name = "set" + StringUtils.upperFirstCharacter(field.fieldName);
                var value = getLanguageTypeValue(field.fieldType, getCell(beginRowIndex, title.columnIndex).val);
                if (value.isEmpty()) {
                    continue;
                }
                sb.append(String.format("            %s(%s);\n", name, value));
            }
            sb.append("        }});\n");
        }
        return sb.toString();
    }

    private ConvertCell getCell(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).cells.get(columnIndex);
    }

    private String genInitMethodsInvoke() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getCodeSegmentLimit(); i++) {
            sb.append(String.format("            initData%d(datas);", i));
            if (i != getCodeSegmentLimit() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private int getCodeSegmentLimit() {
        return rows.size() * titles.size() / getMethodRowLimit() + 1;
    }

    private String getPrimaryKeyType() {
        var title = titles.get(0);
        return getLanguageType(title.fieldDefineDataList.get(index).fieldType);
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
            sb.append(String.format("    //%s\n", title.desc));
        }
        sb.append(String.format("    private %s %s;\n", getLanguageType(fieldType), fieldName));
    }

    @Override
    protected String getLanguageTypeString(String type) {
        var lowerFirstCharacterType = StringUtils.lowerFirstCharacter(type);
        if ("str".equals(lowerFirstCharacterType) || "string".equals(lowerFirstCharacterType)) {
            return "String";
        } else if ("void".equals(lowerFirstCharacterType) || "void0".equals(lowerFirstCharacterType)) {
            return "Void0";
        } else if ("func".equals(lowerFirstCharacterType) || "func0".equals(lowerFirstCharacterType)) {
            return "Func0";
        } else if (lowerFirstCharacterType.startsWith("list") || lowerFirstCharacterType.startsWith("map")) {
            return StringUtils.upperFirstCharacter(type);
        } else if (lowerFirstCharacterType.startsWith("dict")) {
            return lowerFirstCharacterType.replace("dict", "Map");
        }
        return type;
    }

    private String getLanguageTypeValue(String type, String value) {
        value = value.replaceAll("\\n", "");
        var languageType = getLanguageType(type);
        if (value.isEmpty()) {
            return value;
        }

        if (languageType.startsWith("Void") || languageType.startsWith("Func")) {
            StringBuilder sb = new StringBuilder();
            var count = languageType.charAt(4) - '0';
            sb.append('(');
            for (int i = 0; i < count; i++) {
                sb.append("arg").append(i);
                if (i != count - 1) {
                    sb.append(" ,");
                }
            }
            sb.append(") -> { ").append(value).append(" }");
            return sb.toString();
        }

        if (languageType.startsWith("List")) {
            var genericType = getFromGeneric(languageType);
            var valueArr = value.split(",");
            StringBuilder sb = new StringBuilder();
            sb.append("new ArrayList<>(Arrays.asList(");
            for (int i = 0; i < valueArr.length; i++) {
                sb.append(getLanguageTypeValue(genericType, valueArr[i]));
                if (i != valueArr.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("))");
            return sb.toString();
        }

        if (languageType.startsWith("Map")) {
            var generic = getFromGeneric(languageType).split(",");
            var genericKeyType = generic[0];
            var genericValueType = generic[1];

            StringBuilder sb = new StringBuilder();
            var valueArr = value.split(",");
            sb.append("new HashMap<>(){{");
            for (int i = 0; i < valueArr.length; i++) {
                var valArr = valueArr[i].split(":");
                var key = valArr[0];
                var val = valArr[1];
                sb.append("put(");
                sb.append(getLanguageTypeValue(genericKeyType, key));
                sb.append(",");
                sb.append(getLanguageTypeValue(genericValueType, val));
                sb.append(");");
            }
            sb.append("}}");
            return sb.toString();
        }

        return switch (languageType) {
            case "int", "Integer" -> String.valueOf((int) Double.parseDouble(value));
            case "String" -> '"' + value + '"';
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

    @Override
    public String setTemplate() {
        return """
                // Version: {${VERSION}}, Auto convert from file_name=${INPUT_FILE_NAME}, sheet_name=${INPUT_SHEET_NAME}
                package com.wyw.config;
                                
                import java.util.*;
                                
                ${CUSTOM_BLOCK_START}
                ${CUSTOM_BLOCK_END}
                                
                public class ${CLASS_NAME} extends BaseConfig {
                                
                ${FIELD_DECLARE}
                                
                    public static Map<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}> DATAS;
                    
                    public static ${CLASS_NAME} get(${PRIMARY_KEY_TYPE} id) {
                        return DATAS.get(id);
                    }
                    
                ${CUSTOM_BLOCK_START2}
                    private static void preInit(HashMap<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}> data) {
                    }
                    
                    private static void postInit() {
                    }
                ${CUSTOM_BLOCK_END2}
                    
                    static {
                        try {
                            init();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                                
                    private static void init() throws Throwable {
                        var data = new HashMap<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}>(${ROWS});
                        preInit(data);
                        initDatas(data);
                        DATAS = data;
                        postInit();
                    }
                    
                    private static void initDatas(Map<${PRIMARY_KEY_TYPE}, ${CLASS_NAME}> datas) {
                        for(var i = 0; i < ${CODE_SEGMENT_LIMIT}; i++) {
                ${INIT_METHODS_INVOKE}
                        }
                    }
                    
                ${INIT_METHODS}
                                
                ${FIELD_GETTER_SETTER}
                }
                """;
    }
}
