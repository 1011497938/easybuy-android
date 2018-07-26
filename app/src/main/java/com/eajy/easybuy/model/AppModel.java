package com.eajy.easybuy.model;

import android.content.Context;
import android.util.Log;

import com.eajy.easybuy.info.UserInfo;
import com.eajy.easybuy.util.NetUtils;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppModel{
    public final static String BASE_URL = "http://10.180.85.132:8000";
    public static ACache mCache;

    public static void init(Context mainActivity){
        mCache = ACache.get(mainActivity);
//        login("神奇小螺号", "18867100389", "6666665", "6666664", 666663, "6666662");
//        http://10.180.94.45:8000/manage_d/?token=194582ad894aeca781c864a5be456081&did=01234568&state=10&address=%E7%8E%89%E6%B3%89&hint=%E5%8E%A8%E6%88%BF
    }

    public static synchronized boolean isLogin(){
        Object isLogin = mCache.getAsObject("isLogin");
        if (isLogin!=null)
            return (boolean)isLogin;
        else
            return false;
    }

    public static synchronized void setPassword(String password){
        mCache.put("password", password);

    }

    public static synchronized boolean isPasswordEqual(String password){
        return password.equals(mCache.getAsString("password"));
    }

    public static synchronized void logout(){
        mCache.put("isLogin", false);
    }

    public static  synchronized void login(String name, String phone, String card, String token, int money, String id, int limit)  {
        mCache.put("isLogin", true);
        mCache.put("name", name);
        mCache.put(name + "_phone", phone);
        mCache.put(name + "_card", card);
        mCache.put("token", token);
        mCache.put("money", money);
        mCache.put("id", id);
        mCache.put(name + "_limit", limit);
    }

    public static synchronized UserInfo getUserInfo(){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(mCache.getAsString("name"));
        userInfo.setAccount(mCache.getAsString("card"));
        userInfo.setMoney((int)mCache.getAsObject("money"));
        userInfo.setToken(mCache.getAsString("token"));
        userInfo.setId(mCache.getAsString("id"));
        userInfo.setPhone(mCache.getAsString("phone"));
        return  userInfo;
    }

    public static synchronized String getCard(){
        if (mCache.getAsString(getUserName() + "_card")!=null)
            return  mCache.getAsString(getUserName() + "_card");
        else
            return "";
    }

    public static synchronized void setCard(String card){
        mCache.put(getUserName() + "_card", card);
    }

    public static synchronized String getPhone(){
        if(isLogin())
            return mCache.getAsString(getUserName() + "_phone");
        else
            return "";
    }

    public static synchronized int getLimitNum(){
        if (mCache.getAsObject(getUserName() + "_limit")!=null)
            return (int)mCache.getAsObject(getUserName() + "_limit");
        else
            return -1;
    }

    public static synchronized String getToken(){
//        if (isLogin())
            return mCache.getAsString("token");
//        else
//            return "";
    }

    public static synchronized  void setPhone(String phone){
        if (isLogin()){
            mCache.put(getUserName() + "_phone", phone);
        }else{
            Log.e("WTF", "不要尝试在未登陆的时候修改手机号");
        }
    }

    public static synchronized void setUserName(String name){
        if (isLogin())
           mCache.put("name", name);
    }

    public static synchronized String getUserName(){
        return mCache.getAsString("name");
    }

    public static boolean isPhoneValid(String userName) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(userName);
        return m.matches() && userName.length() >= 7 && userName.length() <= 12;
    }

    public static boolean isCardNumValid(String cardNum) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(cardNum);
        //与银行卡交互
        return m.matches() && cardNum.length() >= 7 && cardNum.length() <= 30;
    }

    public static boolean isNum(String num) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(num);
        return m.matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 4 && password.length() <= 20;
    }

    public static boolean isNameValid(String name) {
        return name.length() >= 3 && name.length() <= 20;
    }

    public static int getGoodsPrice(String name, int retry_time){
        try {
            String response = NetUtils.get(AppModel.BASE_URL + "/goods/?name=" + name);
            Log.d("WTF", "物品" + name + response);
            JSONObject jsonObject = new JSONObject(response);
            return Integer.parseInt(jsonObject.optString("market_price"));
        }catch (Exception e){
            e.printStackTrace();
            if (retry_time>0){
                return getGoodsPrice(name, retry_time-1);
            }
            else
                retry_time = 10;
            return 1500;
        }
    }
}
