package com.wyw.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader.Feature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUtil {
    private JsonUtil(){}

    public static <T> T parseObject(String pathString, Class<T> clazz) {
        Path path = Path.of(pathString);
        if (!Files.exists(path)) {
            System.out.printf("Path: %s not exists!\n", pathString);
            return null;
        }

        try {
            return JSONObject.parseObject(Files.readString(path), clazz, Feature.SupportSmartMatch);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject parseObject(String pathString) {
        Path path = Path.of(pathString);
        if (!Files.exists(path)) {
            System.out.printf("Path: %s not exists!\n", pathString);
            return null;
        }

        try {
            return JSONObject.parseObject(Files.readString(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T getObject(JSONObject jsonObject, String key, Class<T> clazz) {
        return jsonObject.getObject(key, clazz, Feature.SupportSmartMatch);
    }
}
