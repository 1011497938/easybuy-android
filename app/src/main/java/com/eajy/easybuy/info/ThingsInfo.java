package com.eajy.easybuy.info;

public class ThingsInfo {
    private String id = "";
    private String name = "";   //type
    private String pos = "";    //hint
    private String address = "";
    private String goodName = "";  //盐
    private int remian = 100;  //state
    private boolean isChoosen = false;
    public  boolean oldIsChoosen;

    public synchronized String getName() {
        return name;
    }

    public synchronized String getPos() {
        return pos;
    }

    public synchronized int getRemain() {
        return remian;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized void setPos(String pos) {
        this.pos = pos;
    }

    public synchronized void  setRemain(int remain) {
        this.remian = remain;
    }



    public synchronized String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        this.id = id;
    }

    public synchronized String getAddress() {
        return address;
    }

    public synchronized void setAddress(String address) {
        this.address = address;
    }

    public synchronized String getGoodName() {
        return goodName;
    }

    public synchronized void setGoodName(String goodName) {
        this.goodName = goodName;
    }


    public synchronized int getRemian() {
        return remian;
    }

    public synchronized void setRemian(int remian) {
        this.remian = remian;
    }

    public synchronized boolean isChoosen() {
        return isChoosen;
    }

    public synchronized void setChoosen(boolean choosen) {
        oldIsChoosen = isChoosen;
        isChoosen = choosen;
    }

    public  synchronized void reset(){
        isChoosen = oldIsChoosen;
    }

}
