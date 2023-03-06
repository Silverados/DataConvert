package com.wyw.serialize;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InputDO {
    public InputHead head;
    public InputBody body;
    public Tag tag;
    public OutputFile outputFile;
}
