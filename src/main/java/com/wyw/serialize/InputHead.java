package com.wyw.serialize;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class InputHead {
    public List<String> exportFileName;
    public InputHead() {
        exportFileName = new ArrayList<>(4);
    }

    public InputHead add(String element) {
        exportFileName.add(element);
        return this;
    }
}
