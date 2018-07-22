package com.eajy.materialdesigndemo.info;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderInfo {

    private String id;
    private int amount; //金额总量
    private String receiver;  //签收人姓名
    private String rPhone;  //签收人手机号


    private ArrayList<String> logisticsTime = new ArrayList<String>();  //物流信息时间
    private ArrayList<String> logisticsPos = new ArrayList<String>();  //物流信息地点

    private String goodName = "";

    private String date = "";
    private String price = "";
    private String state = "";

    private boolean isArrived;
    private String address = "";
    private String materialPos = "";

    private int num = 0;


    public synchronized ArrayList<String> getLogisticsTime() {
        return logisticsTime;
    }

    public synchronized void setLogisticsTime(ArrayList<String> logisticsTime) {
        this.logisticsTime = logisticsTime;
    }

    public synchronized ArrayList<String> getLogisticsPos() {
        return logisticsPos;
    }

    public synchronized void setLogisticsPos(ArrayList<String> logisticsPos) {
        this.logisticsPos = logisticsPos;
    }

    public synchronized String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        this.id = id;
    }

    public synchronized int getAmount() {
        return amount;
    }

    public synchronized void setAmount(int amount) {
        this.amount = amount;
    }

    public synchronized String getReceiver() {
        return receiver;
    }

    public synchronized void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public synchronized String getrPhone() {
        return rPhone;
    }

    public synchronized void setrPhone(String rPhone) {
        this.rPhone = rPhone;
    }


    public synchronized String getGoodName() {
        return goodName;
    }

    public synchronized void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public synchronized String getDate() {
        return date;
    }

    public synchronized void setDate(String date) {
        this.date = date;
    }

    public synchronized String getPrice() {
        return price;
    }

    public synchronized void setPrice(String price) {
        this.price = price;
    }

    public synchronized String getState() {
        return state;
    }

    public synchronized void setState(String state) {
        this.state = state;
    }

    public synchronized String getAddress() {
        return address;
    }

    public synchronized void setAddress(String address) {
        this.address = address;
    }

    public synchronized String getMaterialPos() {
        return materialPos;
    }

    public synchronized void setMaterialPos(String materialPos) {
        this.materialPos = materialPos;
    }

    public synchronized boolean isArrived() {
        return isArrived;
    }

    public synchronized void setArrived(boolean arrived) {
        isArrived = arrived;
    }

    public synchronized int getNum() {
        return num;
    }

    public synchronized void setNum(int num) {
        this.num = num;
    }
}


