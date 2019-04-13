package com.example.litepaltest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.githang.statusbar.StatusBarCompat;

import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lenovo on 2018/2/27.
 */

public class New_Time_Activity extends AppCompatActivity {
    private Button submit;
    private Button back;

    private EditText input_text;
    private EditText input_note;
    private RadioGroup radioGroup_category;
    private RadioButton radioButton_category;
    private DatePicker input_startDate;
    private TimePicker input_startTime;
    private DatePicker input_endDate;
    private TimePicker input_endTime;
    private DBhelper dBhelper = new DBhelper();

    private String name;
    private String oldID;
    private Date startTime;
    private Date endTime;
    private String note;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_time);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#1000FF00"),true );

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.hide();
        }

        //设置id、初始化
        setID();
        initData();

        //按下确认键
        submitListener();
        //按下返回键
        backListener();

    }

    //返回键监听
    private void backListener(){
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //返回待办界面
                finish();
            }
        });
    }

    //确认键监听
    private void submitListener(){
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //获取用户输入内容
                getInput();
                //添加时间记录
                if (!name.equals("") && category != null) {
                    if (oldID == null) {
                        if (dBhelper.addTime(name, note, category, startTime, endTime, -1.0, -1.0) != null) {
                            if (category.equals("学习")){
                                SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                                UpdateStudyTime(pref.getString("ID", ""));
                            }
                            finish();   //关闭此界面
                        }
                        //
                    }
                    else{
                        if (dBhelper.updateTime(oldID, name, note, category, startTime, endTime)){
                            if (category.equals("学习")){
                                SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
                                UpdateStudyTime(pref.getString("ID", ""));
                            }
                            finish();
                        }
                        //名字已存在/时间冲突/结束时间必须大于开始时间
                    }
                }
            }
        });
    }

    //获取用户输入内容
    private void getInput(){
        get_name();
        get_category();
        get_startTime();
        get_endTime();
        get_note();
    }

    //获取名称
    private void get_name(){
        name = input_text.getText().toString();
    }

    //获取备注
    private void get_note(){
        note = input_note.getText().toString();
    }

    //获取种类
    private void get_category(){
        radioButton_category = (RadioButton)findViewById(radioGroup_category.getCheckedRadioButtonId());
        if (radioButton_category != null) {
            category = radioButton_category.getText().toString();
        }
    }

    //获取开始时间
    private void get_startTime(){
        int year = input_startDate.getYear() - 1900;
        int month = input_startDate.getMonth();
        int day = input_startDate.getDayOfMonth();
        int hour = input_startTime.getCurrentHour();
        int minute = input_startTime.getCurrentMinute();
        startTime = new Date(year, month, day, hour, minute);
    }

    //获取结束时间
    private void get_endTime(){
        int year = input_endDate.getYear() - 1900;
        int month = input_endDate.getMonth();
        int day = input_endDate.getDayOfMonth();
        int hour = input_endTime.getCurrentHour();
        int minute = input_endTime.getCurrentMinute();
        endTime = new Date(year, month, day, hour, minute);
    }

    //设置Id
    private void setID(){
        //设置timepicker时间格式为24小时制
        input_startTime = (TimePicker)findViewById(R.id.begin_time);
        input_startTime.setIs24HourView(true);
        input_endTime = (TimePicker)findViewById(R.id.terminal_time);
        input_endTime.setIs24HourView(true);

        input_startDate = (DatePicker)findViewById(R.id.begin_date);
        input_endDate = (DatePicker)findViewById(R.id.terminal_date);
        radioGroup_category = (RadioGroup)findViewById(R.id.radiotype);
        input_note = (EditText)findViewById(R.id.note);
        input_text = (EditText)findViewById(R.id.text);
        submit = (Button) findViewById(R.id.time_title_submit);
        back = (Button)findViewById(R.id.time_title_back);
    }

    //设置初始数据
    private void initData(){
        Bmob.initialize(this, "75c88783b55ef9650c609d100ef036b4" );
        Intent intent = getIntent();
        oldID = intent.getStringExtra("id_extra");
        if(oldID != null){
            InTimeList oldTime = dBhelper.findTimeById(oldID);

            input_text.setText(oldTime.getName());
            input_note.setText(oldTime.getNote());

            startTime = oldTime.getStartTime();
            input_startDate.updateDate(startTime.getYear() + 1900, startTime.getMonth(), startTime.getDate());
            input_startTime.setCurrentHour(startTime.getHours());
            input_startTime.setCurrentMinute(startTime.getMinutes());
            endTime = oldTime.getEndTime();
            input_endDate.updateDate(endTime.getYear() + 1900, endTime.getMonth(), endTime.getDate());
            input_endTime.setCurrentHour(endTime.getHours());
            input_endTime.setCurrentMinute(endTime.getMinutes());
        }
    }

    //更新学习时间
    private void UpdateStudyTime(String objectID) {
        int TimeSum = dBhelper.getCategoryTimeSum("学习");
        SharedPreferences pref = getSharedPreferences("LoginData", MODE_PRIVATE);
        MyUser user = new MyUser();
        user.setObjectId(pref.getString("ID", ""));
        user.setUsername(pref.getString("userName", ""));
        user.setPassword(pref.getString("password", ""));
        user.setMobilePhoneNumber(pref.getString("cellphone", ""));
        user.setSign(pref.getString("signiture", ""));
        user.setStudyTime(TimeSum);
        user.update(objectID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "更新成功");
                } else {
                    Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

}
