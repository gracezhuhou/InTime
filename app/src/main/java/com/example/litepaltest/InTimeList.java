package com.example.litepaltest;

import android.provider.ContactsContract;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by lenovo on 2018/1/22.
 */

public class InTimeList extends DataSupport{
    private int id;
    private String name;
    private String note;    //备注
    private String category;
    private Date startTime = new Date(System.currentTimeMillis()); //默认为现在时间
    private Date endTime = new Date(System.currentTimeMillis());
    private double Lng;  //经度
    private double Lat;  //纬度

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getNote(){
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public double getLng(){
        return Lng;
    }

    public void setLng(double Lng){
        this.Lng = Lng;
    }

    public double getLat(){
        return Lat;
    }

    public void setLat(double Lat){
        this.Lat = Lat;
    }
}
