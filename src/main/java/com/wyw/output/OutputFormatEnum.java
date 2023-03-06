package com.wyw.output;

import java.util.function.Supplier;

public enum OutputFormatEnum {
    Java(JavaOutputFormat::new, "java"),
    CSharp(CSharpOutputFormat::new, "cs");

    private final Supplier<OutputFormat> noArgConstructor;
    public final String fileExtension;
    OutputFormatEnum(Supplier<OutputFormat> constructor, String fileExtension) {
        this.noArgConstructor = constructor;
        this.fileExtension = fileExtension;
    }

    public Supplier<OutputFormat> getNoArgConstructor() {
        return noArgConstructor;
    }
}
