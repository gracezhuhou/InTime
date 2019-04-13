package com.example.litepaltest;

import cn.bmob.v3.BmobUser;

/**
 * Created by ASUS on 2018/3/24.
 */

public class MyUser extends BmobUser {

    private String sign;
    private int StudyTime;


    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign= sign;
    }

    public int getStudyTime() {
        return StudyTime;
    }

    public void setStudyTime(int studytime) {
        this.StudyTime= studytime;
    }

}