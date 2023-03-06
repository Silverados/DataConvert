package com.wyw.output;

import java.io.IOException;

public class CSharpOutputFormat extends OutputFormat {
    @Override
    public String setTemplate() {
        return """
                public namespace Test {
                
                }
                """;
    }
}
