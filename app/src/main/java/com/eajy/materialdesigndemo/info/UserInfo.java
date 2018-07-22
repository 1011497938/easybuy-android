package com.eajy.materialdesigndemo.info;

public class UserInfo {
    private String userName;

    private String id;
    private String account;  //卡号
    private int money;
    private String token;

    private String phone;

    public synchronized String getUserName() {
        return userName;
    }

    public synchronized void setUserName(String userName) {
        this.userName = userName;
    }

    public synchronized String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        this.id = id;
    }

    public synchronized String getAccount() {
        return account;
    }

    public synchronized void setAccount(String account) {
        this.account = account;
    }

    public synchronized int getMoney() {
        return money;
    }

    public synchronized void setMoney(int money) {
        this.money = money;
    }

    public synchronized String getToken() {
        return token;
    }

    public synchronized void setToken(String token) {
        this.token = token;
    }

    public synchronized String getPhone() {
        return phone;
    }

    public synchronized void setPhone(String phone) {
        this.phone = phone;
    }
}
