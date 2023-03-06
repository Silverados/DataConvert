package com.wyw;

import com.alibaba.fastjson2.JSONObject;
import com.wyw.serialize.ConvertConfig;
import com.wyw.util.JsonUtil;

import java.util.List;

public class AppContext {
    public ConvertConfig convertConfig;
    public JSONObject tags;
    public String tag;
    public List<Boolean> isExport;
    public boolean mergeSource;

    private AppContext() {
        convertConfig = JsonUtil.parseObject("etc/config.json", ConvertConfig.class);
        tags = JsonUtil.parseObject("etc/tags.json");
    }

    public static AppContext getInstance() {
        return ContextHolder.INSTANCE;
    }

    private static class ContextHolder {
        public static final AppContext INSTANCE = new AppContext();
    }
}
