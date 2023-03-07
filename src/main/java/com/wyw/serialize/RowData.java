package com.wyw.serialize;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class RowData {
    public int rowIndex;
    public Map<Integer, ConvertCell> cells;
}
