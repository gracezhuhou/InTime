package com.example.litepaltest;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by lenovo on 2018/1/22.
 */

public class ToDoList extends DataSupport {
    private int id = 0;
    private String name;
    private int priority;   //优先度
    private Date alarm; //提醒时间
    private Date deadline;
    private long CostTime = 0;
    private String note;    //备注
    private String category;    //类别：学习、工作
    private boolean isfinished = false;
    private boolean isRecording = false;

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public long getCostTime() {
        return CostTime;
    }

    public void setCostTime(long costTime) {
        CostTime += costTime;
    }   //累加

    public String getNote(){
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

    public Date getAlarm(){
        return alarm;
    }

    public void setAlarm(Date alarm) {
        this.alarm = alarm;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsfinished(){
        return isfinished;
    }

    public void setIsfinished(boolean isfinished){
        this.isfinished = isfinished;
    }

    public Boolean getIsRecording(){
        return isRecording;
    }

    public void setIsRecording(boolean isRecording){
        this.isRecording = isRecording;
    }

}
