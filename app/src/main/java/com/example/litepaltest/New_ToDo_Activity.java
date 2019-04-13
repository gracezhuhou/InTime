package com.example.litepaltest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.Date;

public class New_ToDo_Activity extends AppCompatActivity {

    private Button submit;
    private Button back;

    private EditText input_text;
    private RadioGroup radioGroup_priority;
    private RadioButton radioButton_priority;
    private RadioGroup radioGroup_category;
    private RadioButton radioButton_category;
    private DatePicker input_deadline;
    private TimePicker input_alarm;
    private EditText input_note;
    private DBhelper dBhelper = new DBhelper();

    private String name;
    private int priority = 0;
    private Date alarm;
    private Date deadline;
    private String note;
    private String category;
    private String oldname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_todo);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#50EED2EE"),true );

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.hide();
        }

        setID();

        //按下确认键
        submitListener();
        //按下返回键
        backListener();

        //更改数据的情况下自动填充原来的数据
        initData();

    }

    //确认键监听
    private void submitListener(){
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //获取用户输入内容
                getInput();
                //添加代办事项
                if (!name.equals("") && priority != 0 && category != null) {
                    if (oldname == null) {
                        if (dBhelper.addToDoList(name, priority, alarm, deadline, note, category) != null) {
                            finish();
                        }
                        //
                    }
                    else{
                        if (dBhelper.updateToDoList(oldname, name, priority, alarm, deadline, note, category)) {
                            finish();
                        }
                        //
                    }
                }
            }
        });
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

    //获取用户输入内容
    private void getInput(){
        get_name();
        get_priority();
        get_category();
        get_deadline();
        get_alarm();
        get_note();
    }

    //获取待办事项名称
    private void get_name(){
        name = input_text.getText().toString();
    }

    //获取待办事项优先级
    private void get_priority(){
        radioButton_priority =(RadioButton)findViewById(radioGroup_priority.getCheckedRadioButtonId());
        if (radioButton_priority != null) {
            String input_priority = radioButton_priority.getText().toString();
            switch (input_priority) {
                case "紧急重要":
                    priority = 1;
                case "紧急非重要":
                    priority = 2;
                case "重要非紧急":
                    priority = 3;
                case "非紧急非重要":
                    priority = 4;
            }
        }
    }

    //获取种类
    private void get_category(){
        radioButton_category = (RadioButton)findViewById(radioGroup_category.getCheckedRadioButtonId());
        if (radioButton_category != null) {
            category = radioButton_category.getText().toString();
        }
    }

    //获取截止时间
    private void get_deadline(){
        int year = input_deadline.getYear() - 1900;
        int month = input_deadline.getMonth();
        int day = input_deadline.getDayOfMonth();
        deadline = new Date(year, month, day);
    }

    //获取闹铃时间
    private void get_alarm(){
        int year = input_deadline.getYear() - 1900;
        int month = input_deadline.getMonth();
        int day = input_deadline.getDayOfMonth();

        int hour = input_alarm.getCurrentHour();
        int minute = input_alarm.getCurrentMinute();
        alarm = new Date(year,month,day, hour, minute);
    }

    //获取备注
    private void get_note(){
        note = input_note.getText().toString();
    }

    //设置初始数据
    private void initData(){
        Intent intent = getIntent();
        oldname = intent.getStringExtra("name_extra");
        if(oldname != null){
            ToDoList oldToDo = dBhelper.findToDo(oldname);

            input_text.setText(oldToDo.getName());
            input_note.setText(oldToDo.getNote());
            input_deadline.updateDate(oldToDo.getDeadline().getYear() + 1900, oldToDo.getDeadline().getMonth(), oldToDo.getDeadline().getDate());
            if (oldToDo.getAlarm() != null) {
                input_alarm.setCurrentHour(oldToDo.getAlarm().getHours());
                input_alarm.setCurrentMinute(oldToDo.getAlarm().getMinutes());
            }
        }
    }

    //设置Id
    private void setID(){
        submit = (Button) findViewById(R.id.todo_title_submit);
        back = (Button)findViewById(R.id.todo_title_back);
        input_text = (EditText)findViewById(R.id.edit_text);
        radioGroup_priority =(RadioGroup)findViewById(R.id.edit_radiopriority);
        radioGroup_category = (RadioGroup)findViewById(R.id.edit_radiotype);
        input_alarm = (TimePicker)findViewById(R.id.todo_alarm);
        //设置timepicker时间格式为24小时制
        input_alarm.setIs24HourView(true);
        input_deadline = (DatePicker)findViewById(R.id.deadline);
        input_note = (EditText)findViewById(R.id.note);
    }

}


