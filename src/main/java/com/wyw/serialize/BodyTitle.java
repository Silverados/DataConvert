package com.wyw.serialize;


import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class BodyTitle {
    public int columnIndex;
    public String desc;
    public List<FieldDefineData> fieldDefineDataList;
}
