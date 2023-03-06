// Version: {2023-03-06T11:48:54.060978800}, Auto convert from file_name=demo.xlsx, sheet_name=Sheet1
package com.wyw.config;

import java.util.*;

//====================CUSTOM BLOCK START====================
//====================CUSTOM BLOCK END====================

public class Test extends BaseConfig {

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
    private Void0 v0;
    private Void1<Map<String,Integer>> v11;
    private Void1<Integer> v1;
    private Void2<Integer,String> v2;
    private Void3<Integer,Long,String> v3;
    private Func0<Map<String,Integer>> f00;
    private Func0<String> f0;
    private Func1<String,String> f1;
    private Func2<String,String,String> f2;
    private Func3<String,String,String,String> f3;
    private List<Integer> l1;
    private List<String> l2;
    private Map<Integer,Integer> m11;
    private Map<String,Integer> m1;
    private Map<Integer,String> m2;
    private int formula;


    public static Map<String, Test> DATAS;

    public static Test get(String id) {
        return DATAS.get(id);
    }

//====================CUSTOM BLOCK 2 START====================
    private static void preInit(HashMap<String, Test> data) {
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
        var data = new HashMap<String, Test>(8);
        preInit(data);
        initDatas(data);
        DATAS = data;
        postInit();
    }

    private static void initDatas(Map<String, Test> datas) {
        for(var i = 0; i < 1; i++) {
           initData0(datas);
        }
    }

    private static void initData0(Map<String, Test> datas){
        datas.put("1", new Test() {{
            setA(1);
            setB(2L);
            setC(3F);
            setD("4");
            setE("5");
            setF("6");
            setV0(() -> { System.out.println("hello"); });
            setV11((arg0) -> { arg0.put("k",1); });
            setV1((arg0) -> { System.out.println("hello:"+arg0); });
            setF00(() -> { returnnewHashMap<>(); });
            setF0(() -> { return"helloWorld"; });
            setF1((arg0) -> { returnarg0; });
            setF2((arg0 ,arg1) -> { returnarg0+arg1; });
            setF3((arg0 ,arg1 ,arg2) -> { returnarg0+arg1+arg2; });
            setL1(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6)));
            setL2(new ArrayList<>(Arrays.asList("apple", "banana", "pear")));
            setM11(new HashMap<>(){{put(1,1);put(2,2);}});
            setM1(new HashMap<>(){{put("a",1);put("b",2);}});
            setM2(new HashMap<>(){{put(1,"1");put(1,"2");}});
            setFormula(2147483647);
        }});
        datas.put("2", new Test() {{
            setA(2);
            setB(3L);
            setC(4F);
            setD("5");
            setE("6");
            setF("7");
            setM11(new HashMap<>(){{put(3,4);}});
        }});
        datas.put("3", new Test() {{
            setA(3);
            setB(4L);
            setC(5F);
            setD("6");
            setE("7");
            setF("8");
        }});
        datas.put("4", new Test() {{
            setA(4);
            setB(5L);
            setC(6F);
            setD("7");
            setE("8");
            setF("9");
        }});
        datas.put("5", new Test() {{
            setA(5);
            setB(6L);
            setC(7F);
            setD("8");
            setE("9");
            setF("10");
        }});
        datas.put("6", new Test() {{
            setA(6);
            setB(7L);
            setC(8F);
            setD("9");
            setE("10");
            setF("11");
        }});
        datas.put("7", new Test() {{
            setA(7);
            setB(8L);
            setC(9F);
            setD("10");
            setE("11");
            setF("12");
        }});
        datas.put("8", new Test() {{
            setA(8);
            setB(9L);
            setC(10F);
            setD("11");
            setE("12");
            setF("13");
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

    public void setV0(Void0 v0) {
        this.v0 = v0;
    }

    public Void0 getV0() {
        return v0;
    }

    public void setV11(Void1<Map<String,Integer>> v11) {
        this.v11 = v11;
    }

    public Void1<Map<String,Integer>> getV11() {
        return v11;
    }

    public void setV1(Void1<Integer> v1) {
        this.v1 = v1;
    }

    public Void1<Integer> getV1() {
        return v1;
    }

    public void setV2(Void2<Integer,String> v2) {
        this.v2 = v2;
    }

    public Void2<Integer,String> getV2() {
        return v2;
    }

    public void setV3(Void3<Integer,Long,String> v3) {
        this.v3 = v3;
    }

    public Void3<Integer,Long,String> getV3() {
        return v3;
    }

    public void setF00(Func0<Map<String,Integer>> f00) {
        this.f00 = f00;
    }

    public Func0<Map<String,Integer>> getF00() {
        return f00;
    }

    public void setF0(Func0<String> f0) {
        this.f0 = f0;
    }

    public Func0<String> getF0() {
        return f0;
    }

    public void setF1(Func1<String,String> f1) {
        this.f1 = f1;
    }

    public Func1<String,String> getF1() {
        return f1;
    }

    public void setF2(Func2<String,String,String> f2) {
        this.f2 = f2;
    }

    public Func2<String,String,String> getF2() {
        return f2;
    }

    public void setF3(Func3<String,String,String,String> f3) {
        this.f3 = f3;
    }

    public Func3<String,String,String,String> getF3() {
        return f3;
    }

    public void setL1(List<Integer> l1) {
        this.l1 = l1;
    }

    public List<Integer> getL1() {
        return l1;
    }

    public void setL2(List<String> l2) {
        this.l2 = l2;
    }

    public List<String> getL2() {
        return l2;
    }

    public void setM11(Map<Integer,Integer> m11) {
        this.m11 = m11;
    }

    public Map<Integer,Integer> getM11() {
        return m11;
    }

    public void setM1(Map<String,Integer> m1) {
        this.m1 = m1;
    }

    public Map<String,Integer> getM1() {
        return m1;
    }

    public void setM2(Map<Integer,String> m2) {
        this.m2 = m2;
    }

    public Map<Integer,String> getM2() {
        return m2;
    }

    public void setFormula(int formula) {
        this.formula = formula;
    }

    public int getFormula() {
        return formula;
    }

}
