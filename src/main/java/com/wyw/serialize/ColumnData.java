package com.wyw.serialize;

import java.util.List;

public class ColumnData {
    public String desc;
    public FieldDefineData serverFieldDefine;
    public FieldDefineData clientFieldDefine;
    public List<ConvertCell> cells;
}
