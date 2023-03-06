package com.wyw.input;

public class InputParserFactory {

    public static InputParser getInputParser(String fileExtension) {
        return InputFileExtensionEnum.getConstructor(fileExtension).get();
    }
}
