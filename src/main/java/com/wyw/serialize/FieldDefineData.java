package com.wyw.serialize;

import lombok.ToString;

@ToString
public class FieldDefineData {
    public String fieldName;
    public String fieldType;

    public FieldDefineData(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }
}
