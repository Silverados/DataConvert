package com.wyw.input;

import java.util.function.Supplier;

public enum InputFileExtensionEnum {
    XLS(XlsInputParser::new),
    XLSX(XlsxInputParser::new);

    private final Supplier<InputParser> noArgConstructor;
    InputFileExtensionEnum(Supplier<InputParser> noArgConstructor) {
        this.noArgConstructor = noArgConstructor;
    }

    public static Supplier<InputParser> getConstructor(InputFileExtensionEnum inputFileExtensionEnum) {
        return inputFileExtensionEnum.noArgConstructor;
    }

    public static Supplier<InputParser> getConstructor(String enumName) {
        return getConstructor(InputFileExtensionEnum.valueOf(enumName.toUpperCase()));
    }
}
