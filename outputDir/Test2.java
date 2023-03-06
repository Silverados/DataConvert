// Version: {2023-03-06T11:48:54.078979900}, Auto convert from file_name=demo.xlsx, sheet_name=Sheet2
package com.wyw.config;

import java.util.*;

//====================CUSTOM BLOCK START====================
//====================CUSTOM BLOCK END====================

public class Test2 extends BaseConfig {

    //id
    private String id;
    //整数int
    private int a;
    //整数long
    private long b;
    //小数
    private float c;
    //字符串
    private String d;
    //字符串
    private String e;
    //字符串
    private String f;


    public static Map<String, Test2> DATAS;

    public static Test2 get(String id) {
        return DATAS.get(id);
    }

//====================CUSTOM BLOCK 2 START====================
    private static void preInit(HashMap<String, Test2> data) {
    }

    private static void postInit() {
    }
//====================CUSTOM BLOCK 2 END====================

    static {
        try {
            init();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void init() throws Throwable {
        var data = new HashMap<String, Test2>(2);
        preInit(data);
        initDatas(data);
        DATAS = data;
        postInit();
    }

    private static void initDatas(Map<String, Test2> datas) {
        for(var i = 0; i < 1; i++) {
           initData0(datas);
        }
    }

    private static void initData0(Map<String, Test2> datas){
        datas.put("1", new Test2() {{
            setA(1);
            setB(2L);
            setC(3F);
            setD("4");
            setE("5");
            setF("6");
        }});
        datas.put("2", new Test2() {{
            setA(2);
            setB(3L);
            setC(4F);
            setD("5");
            setE("6");
            setF("7");
        }});
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public void setB(long b) {
        this.b = b;
    }

    public long getB() {
        return b;
    }

    public void setC(float c) {
        this.c = c;
    }

    public float getC() {
        return c;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getD() {
        return d;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getE() {
        return e;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getF() {
        return f;
    }

}
