package com.example.litepaltest;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.githang.statusbar.StatusBarCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class New_Calendar_Activity extends AppCompatActivity {

    private  Button back;
    private Button submit;

    private EditText input_text;
    private EditText input_location;
    private EditText input_note;
    private RadioGroup radioGroup_category;
    private RadioButton radioButton_category;
    private DatePicker input_startDate;
    private TimePicker input_startTime;
    private DatePicker input_endDate;
    private TimePicker input_endTime;
    private TimePicker input_alarm;
    private RadioGroup cycleGroup;
    private RadioButton cycleButton;
    private DBhelper dBhelper = new DBhelper();

    private String name;
    private String oldname;
    private String location;
    private Date startTime;
    private String oldStartTime;
    private Date endTime;
    private Date alarm;
    private String cycle;
    private String note;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_calendar);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#153A5FCD"),true );

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.hide();
        }

        intID();

        //初始化（更改情况）
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
                //添加日程
                if (!name.equals("") && cycle != null && category != null) {
                    if (oldname == null) {
                        if (dBhelper.addCalendarList(name, location, startTime, endTime, alarm, cycle, note, category) != null) {
                            finish();   //关闭此界面
                        }
                        //
                    }
                    else{
                        if (dBhelper.updateCalendarList(oldname, name, location, startTime, endTime, alarm, cycle, note, category)){
                            finish();
                        }
                        //名字已存在/时间冲突/end必须>start
                    }
                }

            }
        });
    }

    //获取用户输入内容
    private void getInput(){
        get_name();
        get_location();
        get_category();
        get_startTime();
        get_endTime();
        get_alarm();
        get_cycle();
        get_note();
    }

    //获取日程名称
    private void get_name(){
        name = input_text.getText().toString();
    }

    //获取地点
    private void get_location(){
        location = input_text.getText().toString();
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

    //获取提醒时间
    private void get_alarm(){
        int hour = input_alarm.getCurrentHour();
        int minute = input_alarm.getCurrentMinute();
        alarm = new Date(0, 0, 0, hour, minute);
    }

    //获取周期
    private void get_cycle(){
        cycleButton = (RadioButton)findViewById(cycleGroup.getCheckedRadioButtonId());

        if (cycleButton != null) {
            String input_cycle = cycleButton.getText().toString();
            switch (input_cycle) {
                case "每年":
                    cycle = "year";
                    break;
                case "每月":
                    cycle = "month";
                    break;
                case "每周":
                    cycle = "week";
                    break;
                case "无":
                    cycle = "none";
            }
        }
    }

    //获取备注
    private void get_note(){
        note = input_note.getText().toString();
    }

    //设置初始数据
    private void initData() {
        Intent intent = getIntent();
        oldStartTime = intent.getStringExtra("startTime_extra");
        oldname = intent.getStringExtra("name_extra");

        if(oldStartTime != null){
            String [] date = oldStartTime.split("-");
            try {
                int year = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1]);
                int day = Integer.parseInt(date[2]);
                Date oldStart = new Date(year - 1900, month - 1, day);
                CalendarList oldCalendar = dBhelper.findCalendar(oldname, oldStart);

                input_text.setText(oldCalendar.getName());
                input_note.setText(oldCalendar.getNote());
                if (oldCalendar.getAlarm() != null){
                    input_alarm.setCurrentHour(oldCalendar.getAlarm().getHours());
                    input_alarm.setCurrentMinute(oldCalendar.getAlarm().getMinutes());
                }
                input_startDate.updateDate(oldCalendar.getStartTime().getYear() + 1900, oldCalendar.getStartTime().getMonth(), oldCalendar.getStartTime().getDate());
                input_endDate.updateDate(oldCalendar.getEndTime().getYear() + 1900 , oldCalendar.getEndTime().getMonth(), oldCalendar.getEndTime().getDate());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void intID(){
        //设置timepicker时间格式为24小时制
        input_startTime = (TimePicker)findViewById(R.id.begin_time);
        input_endTime = (TimePicker)findViewById(R.id.terminal_time);
        input_alarm = (TimePicker)findViewById(R.id.calendar_alarm);
        input_startTime.setIs24HourView(true);
        input_endTime.setIs24HourView(true);
        input_alarm.setIs24HourView(true);

        back = (Button)findViewById(R.id.calendar_title_back);
        submit = (Button) findViewById(R.id.calendar_title_submit);
        input_text = (EditText)findViewById(R.id.input_schedules);
        input_location = (EditText)findViewById(R.id.input_location);
        radioGroup_category = (RadioGroup)findViewById(R.id.calendar_radiotype);
        input_startDate = (DatePicker)findViewById(R.id.begin_date);
        input_endDate = (DatePicker)findViewById(R.id.terminal_date);
        cycleGroup = (RadioGroup)findViewById(R.id.repeat);
        input_note = (EditText)findViewById(R.id.input_other);
    }
}

