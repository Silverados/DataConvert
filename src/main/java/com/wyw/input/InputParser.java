package com.wyw.input;

import com.wyw.AppContext;
import com.wyw.serialize.InputDO;
import com.wyw.serialize.Tag;
import lombok.Setter;

import java.io.File;
import java.util.List;

public abstract class InputParser {

    protected AppContext context;

    protected File inputFile;

    protected Tag tag;

    public abstract List<InputDO> parser();

    public InputParser() {
        context = AppContext.getInstance();
    }

    public void init(File inputFile, Tag tag) {
        this.inputFile = inputFile;
        this.tag = tag;
    }

}
