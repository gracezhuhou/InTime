package com.example.litepaltest;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class TimeActivity extends BaseActivity {
    private Button add_Time;
    private Button start;
    ImageView pic;
    private RecyclerView timeRecylerView;
    private TimeRecyclerAdapter recyclerAdapter;
    private List<InTimeList> timelist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timelist);

        //判断手机是否静止
        //autoListener();
        pref = getSharedPreferences("Recording",MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();

        //点击底部按钮
        bottomListener();

        //点击左上图片
        picListener();

        //初始化界面，载入已建立时间记录
        initTimeList();

        //点击“+”键
        addListener();

        //点击开始键
        startListener();

        //初始化Bmob
        Bmob.initialize(this, "75c88783b55ef9650c609d100ef036b4" );
    }

    //对底部按钮的响应
    private void bottomListener(){
        ToCalendar = (Button) findViewById(R.id.button_Calendar);
        ToHabit = (Button) findViewById(R.id.button_Habit);
        ToTime = (Button) findViewById(R.id.button_Time);
        ToAnalysis = (Button) findViewById(R.id.button_Analysis);
        ToToDo = (Button) findViewById(R.id.button_ToDo);

        ToTime.setBackgroundColor(Color.parseColor("#66ff99"));


        //对"待办"按钮的响应，跳转至日程页面
        ToToDo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.todolist);
                Intent intent = new Intent(TimeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"日程"按钮的响应，跳转至日程页面
        ToCalendar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.calendarlist);
                Intent intent = new Intent(TimeActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"习惯"按钮的响应，跳转至习惯页面
        ToHabit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.habitlist);
                Intent intent = new Intent(TimeActivity.this, HabitActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"分析"按钮的响应，跳转至分析页面
        ToAnalysis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.habitlist);
                Intent intent = new Intent(TimeActivity.this, AnalysisActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //对头像图片的响应
    void picListener(){
        pic = (ImageView)findViewById(R.id.profile);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }

    //增加时间记录
    private void addListener(){
        timeRecylerView = (RecyclerView)findViewById(R.id.time_recyclerview_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        timeRecylerView.setLayoutManager(layoutManager);
        timeRecylerView.addItemDecoration(new TimeItemDecoration(this));
        recyclerAdapter = new TimeRecyclerAdapter(timelist);
        timeRecylerView.setAdapter(recyclerAdapter);
        timeRecylerView.setHasFixedSize(true);
        //用自定义分割线类设置分割线


        add_Time = (Button)findViewById(R.id.title_add);

        add_Time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(TimeActivity.this, New_Time_Activity.class);
                startActivity(intent);
            }
        });
    }

    //计时
    private void startListener(){
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = pref.getBoolean("isRecording", false);
                isAuto = pref.getBoolean("isAuto", false);
                isToDoRecording = pref.getBoolean("isToDoRecording", false);

                if(isToDoRecording){
                    Toast.makeText(TimeActivity.this, "待办事项记录中~", Toast.LENGTH_SHORT).show();
                }
                else if (!isRecording || isAuto) {
                    timeNow = new Date(System.currentTimeMillis());
                    start.setBackgroundResource(R.drawable.end);
                    //isRecording = true;
                    //isAuto = false;
                    editor.putBoolean("isRecording", true);
                    editor.putBoolean("isAuto", false);
                    editor.putInt("year", timeNow.getYear());
                    editor.putInt("month", timeNow.getMonth());
                    editor.putInt("day", timeNow.getDate());
                    editor.putInt("hour", timeNow.getHours());
                    editor.putInt("minute", timeNow.getMinutes());
                    editor.putInt("second", timeNow.getSeconds());
                    editor.apply();
                }
                else{
                    timeEnd = new Date(System.currentTimeMillis());
                    start.setBackgroundResource(R.drawable.play);
                    //isRecording = false;
                    editor.putBoolean("isRecording", false);
                    editor.apply();

                    isRecording = pref.getBoolean("isRecording",false);

                    //增加时间记录
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");

                    int year = pref.getInt("year", 0);
                    int month = pref.getInt("month", 0);
                    int day = pref.getInt("day", 0);
                    int hour = pref.getInt("hour", 0);
                    int minute = pref.getInt("minute", 0);
                    int second = pref.getInt("second", 0);
                    timeNow = new Date(year, month, day, hour, minute, second);

                    InTimeList time = dBhelper.addTime(formatter.format(timeNow),null, null, timeNow, timeEnd, longtiude, latitude);
                    if (time != null) {
                        timelist.add(time);
                        recyclerAdapter.notifyDataSetChanged();
                        timeRecylerView.scrollToPosition(timelist.size() - 1);
                    }
                    else{
                        Toast.makeText(TimeActivity.this, "时间冲突啦", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    //静止时自动记录
    void autoListener() {
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

                if (isRecording && isAuto) {
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

                    if ((timeEnd.getTime() - timeNow.getTime()) * 0.0000167 > 1) {
                        //增加时间记录
                        InTimeList possibleTime = dBhelper.getByLocation(longtiude, latitude);
                        if (possibleTime != null) {
                            InTimeList time =  dBhelper.addTime(possibleTime.getName(), null, possibleTime.getCategory(), timeNow, timeEnd, longtiude, latitude);
                            if (time != null) {
                                timelist.add(time);
                                recyclerAdapter.notifyDataSetChanged();
                                timeRecylerView.scrollToPosition(timelist.size() - 1);
                            }
                            else{
                                Toast.makeText(TimeActivity.this, "时间冲突啦", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
                            InTimeList time = dBhelper.addTime(formatter.format(timeNow), null, null, timeNow, timeEnd, longtiude, latitude);
                            if (time != null) {
                                timelist.add(time);
                                recyclerAdapter.notifyDataSetChanged();
                                timeRecylerView.scrollToPosition(timelist.size() - 1);
                            }
                            else{
                                Toast.makeText(TimeActivity.this, "时间冲突啦", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            public void onStatic() {
                //if (isRecording){
                //    Toast.makeText(BaseActivity.this, "记录ing", Toast.LENGTH_SHORT).show();
                //}

                isRecording = pref.getBoolean("isRecording", false);
                isToDoRecording = pref.getBoolean("isToDoRecording", false);

                if (!isRecording && !isToDoRecording) {
                    timeNow = new Date(System.currentTimeMillis());
                    if (!dBhelper.isTimeConflict(timeNow)) {
                        //isRecording = true;
                        //isAuto = true;
                        editor.putBoolean("isRecording", true);
                        editor.putBoolean("isAuto", true);

                        int year = timeNow.getYear();
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


    //初始化时间轴
    private void initTimeList(){
        start = (Button)findViewById(R.id.start);

        isRecording = pref.getBoolean("isRecording", false);
        isAuto = pref.getBoolean("isAuto", false);

        if(!isRecording || isAuto) {
            start.setBackgroundResource(R.drawable.play);
        }
        else{
            start.setBackgroundResource(R.drawable.end);
        }
        timelist = dBhelper.getTimeList();
    }

    //从New_Time_Activity返回时，刷新界面
    @Override
    protected void onResume(){
        super.onResume();
        timelist.clear();
        timelist.addAll(dBhelper.getTimeList());
        recyclerAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }
}
