package com.wyw.serialize;

import java.util.List;

public class InputBody {
    public List<BodyTitle> titles;
    public List<RowData> rows;

    public InputBody(List<BodyTitle> titles, List<RowData> rows) {
        this.titles = titles;
        this.rows = rows;
    }

}
