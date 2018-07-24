package com.eajy.easybuy.model;

import com.eajy.easybuy.info.GoodInfo;
import com.eajy.easybuy.info.OrderInfo;
import com.eajy.easybuy.info.ThingsInfo;

import java.util.ArrayList;

public class DataStore {
//    洪子翔的
    static boolean globalnfc = true;
    public  synchronized static boolean getglobal() {
        return globalnfc;
    }
    public  synchronized static void setglobaltrue() { globalnfc = true; }
    public  synchronized static void setglobalfalse() { globalnfc = false; }

//    static private boolean isLogin = false;

//    static private UserInfo userInfo = new UserInfo();
    static private String recentlyAddress = "";   //最近使用过的地址

    static private ArrayList<ThingsInfo> allThings = new ArrayList<ThingsInfo>();
    static private ArrayList<OrderInfo> allOrder = new ArrayList<OrderInfo>();
    static private ArrayList<GoodInfo> allGood = new ArrayList<GoodInfo>();

    static private boolean shouldNotifed = true;

//    public  synchronized static UserInfo getUserInfo() {
//        return userInfo;
//    }
//
//    public  synchronized static void setUserInfo(UserInfo userInfo) {
//        DataStore.userInfo = userInfo;
//    }

//    public  synchronized static boolean isIsLogin() {
//        return isLogin;
//    }

//    public  synchronized static void setIsLogin(boolean isLogin) {
//        DataStore.isLogin = isLogin;
//    }

    public  synchronized static String getRecentlyAddress() {
        return recentlyAddress;
    }

    public  synchronized static void setRecentlyAddress(String recentlyAddress) {
        DataStore.recentlyAddress = recentlyAddress;
    }

    public  synchronized static ArrayList<ThingsInfo> getAllThings() {
        return allThings;
    }

    public  synchronized static void setAllThings(ArrayList<ThingsInfo> allThings) {
        DataStore.allThings = allThings;
    }

    public  synchronized static ArrayList<OrderInfo> getAllOrder() {
        return allOrder;
    }

    public  synchronized static void setAllOrder(ArrayList<OrderInfo> allOrder) {
        DataStore.allOrder = allOrder;
    }

    public  synchronized static ArrayList<GoodInfo> getAllGood() {
        return allGood;
    }

    public  synchronized static void setAllGood(ArrayList<GoodInfo> allGood) {
        DataStore.allGood = allGood;
    }

    public  synchronized static boolean isShouldNotifed() {
        return shouldNotifed;
    }

    public  synchronized static void setShouldNotifed(boolean shouldNotifed) {
        DataStore.shouldNotifed = shouldNotifed;
    }
}

