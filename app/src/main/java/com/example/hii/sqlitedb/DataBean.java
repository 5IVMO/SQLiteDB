package com.example.hii.sqlitedb;

/**
 * Created by hii on 2/5/2017.
 */

public class DataBean {
    private int ID;
    private String city;
    private String province;

    public DataBean(){

    }
    public DataBean(int ID, String city, String province) {
        this.ID = ID;
        this.city = city;
        this.province = province;
    }
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
