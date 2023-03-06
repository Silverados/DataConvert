package com.wyw.serialize;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RowData {
    public int rowIndex;
    public List<ConvertCell> cells;
}
