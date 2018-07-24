package com.eajy.easybuy.info;

public class GoodInfo {
    static private  String name;
    static private int price;  //一分为单位
    static private String bref;

    public synchronized static String getName() {
        return name;
    }

    public synchronized static void setName(String name) {
        GoodInfo.name = name;
    }

    public synchronized static int getPrice() {
        return price;
    }

    public synchronized static void setPrice(int price) {
        GoodInfo.price = price;
    }

    public synchronized static String getBref() {
        return bref;
    }

    public synchronized static void setBref(String bref) {
        GoodInfo.bref = bref;
    }


}
