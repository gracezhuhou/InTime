package com.example.litepaltest;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.githang.statusbar.StatusBarCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    Button ToToDo;
    Button ToTime;
    Button ToCalendar;
    Button ToHabit;
    Button ToAnalysis;
    DBhelper dBhelper = new DBhelper();

    static double longtiude;
    static double latitude;
    LocationClient mLocationClient;
    StringBuilder currentPosition = new StringBuilder();

    boolean isToDoRecording;
    boolean isAuto;
    boolean isRecording;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    static Date timeNow;
    static Date timeEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"),true );

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.hide();
        }

        pref = getSharedPreferences("Recording",MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();

        //editor.putBoolean("isToDoRecording", false);
        //editor.putBoolean("isAuto", false);
        //editor.putBoolean("isRecording", false);
        //editor.apply();


        Location();

        //判断手机是否静止
        autoListener();
    }

    //定位初始化
    void Location(){
        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        //positionText = (TextView) findViewById(R.id.position_text_view);
        List<String> permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(BaseActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(BaseActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }

        if (ContextCompat.checkSelfPermission(BaseActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(BaseActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    //定位方法
    void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    //定位精度相关设置
    void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //StringBuilder currentPosition = new StringBuilder();
            longtiude = location.getLongitude();
            latitude = location.getLatitude();
            //currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
            //currentPosition.append("经线：").append(location.getLongitude()).append("\n");
            //currentPosition.append("国家：").append(location.getCountry()).append("\n");
            //currentPosition.append("省：").append(location.getProvince()).append("\n");
            currentPosition.append(location.getCity()).append("\n");
            //currentPosition.append("区：").append(location.getDistrict()).append("\n");
            currentPosition.append(location.getStreet()).append("\n");
            //currentPosition.append(location.getStreetNumber()).append("\n");

            /*currentPosition.append("定位方式：");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }*/
            //positionText.setText(currentPosition);
        }
    }

    //静止时自动记录
    void autoListener(){
        ShakeListener shakeListener = new ShakeListener(this); //创建一个对象
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {       //调用setOnShakeListener方法进行监听
            public void onShake() {
                System.out.println("shake");
                //Toast.makeText(BaseActivity.this, "你在摇哦", Toast.LENGTH_SHORT).show();

                //if (isRecording){
                //    Toast.makeText(BaseActivity.this, "记录ing", Toast.LENGTH_SHORT).show();
                //}

                isRecording = pref.getBoolean("isRecording", false);
                isAuto = pref.getBoolean("isAuto", false);

                if(isRecording && isAuto){
                    timeEnd = new Date(System.currentTimeMillis());
                    //isRecording = false;
                    //isAuto = false;
                    editor.putBoolean("isRecording", false);
                    editor.putBoolean("isAuto", false);
                    editor.apply();

                    int year = pref.getInt("year", 0);
                    int month = pref.getInt("month", 0);
                    int day = pref.getInt("day", 0);
                    int hour = pref.getInt("hour", 0);
                    int minute = pref.getInt("minute", 0);
                    int second = pref.getInt("second", 0);
                    timeNow = new Date(year, month, day, hour, minute, second);
                    if ( (timeEnd.getTime() - timeNow.getTime()) * 0.0000167 > 1) {
                        //增加时间记录
                        InTimeList possibleTime = dBhelper.getByLocation(longtiude,latitude);
                        if (possibleTime != null) {
                            dBhelper.addTime(possibleTime.getName(), null, possibleTime.getCategory(), timeNow, timeEnd, longtiude, latitude);
                        }
                        else {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
                            dBhelper.addTime(formatter.format(timeNow), null, null, timeNow, timeEnd, longtiude, latitude);
                        }
                    }
                }
            }

            public void onStatic(){
                //if (isRecording){
                //    Toast.makeText(BaseActivity.this, "记录ing", Toast.LENGTH_SHORT).show();
                //}

                isRecording = pref.getBoolean("isRecording", false);
                isAuto = pref.getBoolean("isAuto", false);
                isToDoRecording = pref.getBoolean("isToDoRecording", false);

                if (!isRecording && !isToDoRecording) {
                    Date timeNow = new Date(System.currentTimeMillis());
                    if (!dBhelper.isTimeConflict(timeNow)) {
                        //isRecording = true;
                        //isAuto = true;
                        editor.putBoolean("isRecording", true);
                        editor.putBoolean("isAuto", true);
                        editor.putInt("year", timeNow.getYear());
                        editor.putInt("month", timeNow.getMonth());
                        editor.putInt("day", timeNow.getDate());
                        editor.putInt("hour", timeNow.getHours());
                        editor.putInt("minute", timeNow.getMinutes());
                        editor.putInt("second", timeNow.getSeconds());
                        editor.apply();
                    }
                }
            }
        });
    }
}
