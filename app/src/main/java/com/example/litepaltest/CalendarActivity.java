package com.example.litepaltest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.githang.statusbar.StatusBarCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends BaseActivity {

    private Button add_Calendar;
    private ImageView pic;
    private MaterialCalendarView calendarView;
    private RecyclerView calendarRecylerView;
    private CalendarRecyclerAdapter recyclerAdapter;
    private List<CalendarList> calendarlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendarlist);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"),true );

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.hide();
        }

        //点击底部按钮
        bottomListener();

        //点击左上图片
        picListener();

        //初始化界面，载入已建立日程
        initCalendarList();

        //点击任意日期
        calendarListener();

        //点击“+”键
        addListener();
    }

    //对"+"按钮的响应
    private void addListener(){
        //跳转至增加日程页面
        addCalendar();
    }

    //对底部按钮的响应
    private void bottomListener(){
        ToToDo = (Button) findViewById(R.id.button_ToDo);
        ToCalendar = (Button) findViewById(R.id.button_Calendar);
        ToTime = (Button) findViewById(R.id.button_Time);
        ToHabit = (Button) findViewById(R.id.button_Habit);
        ToAnalysis = (Button) findViewById(R.id.button_Analysis);

        ToCalendar.setBackgroundColor(Color.parseColor("#99CCFF"));

        //对"待办"按钮的响应，跳转至日程页面
        ToToDo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.todolist);
                Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"时间轴"按钮的响应，跳转至时间轴页面
        ToTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.timelist);
                Intent intent = new Intent(CalendarActivity.this, TimeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"习惯"按钮的响应，跳转至习惯页面
        ToHabit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.analysis);
                Intent intent = new Intent(CalendarActivity.this, HabitActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"分析"按钮的响应，跳转至分析页面
        ToAnalysis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.habitlist);
                Intent intent = new Intent(CalendarActivity.this, AnalysisActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //对头像图片的响应
    private void picListener(){
        pic = (ImageView)findViewById(R.id.profile);

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, SettingActivity.class);
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

    //增加日程
    private void addCalendar(){
        calendarRecylerView = (RecyclerView)findViewById(R.id.calendar_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        calendarRecylerView.setLayoutManager(layoutManager);
        recyclerAdapter = new CalendarRecyclerAdapter(calendarlist);
        calendarRecylerView.setAdapter(recyclerAdapter);
        calendarRecylerView.setHasFixedSize(true);
        //title_calendar = findViewById(R.id.title_calendar);
        add_Calendar = (Button)findViewById(R.id.title_calendar).findViewById(R.id.title_add);

        add_Calendar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(CalendarActivity.this, New_Calendar_Activity.class);
                startActivity(intent);
            }
        });
    }

    //初始化日程
    private void initCalendarList(){
        calendarlist = dBhelper.getcalendarlist();
    }

    //日程点击监听
    private void calendarListener(){
        calendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        calendarView.setTileHeightDp(45);
        calendarView.setDateSelected(new Date(System.currentTimeMillis()), true);
        calendarView.setArrowColor(Color.parseColor("#99CCFF"));
        calendarView.setSelectionColor(Color.parseColor("#99CCFF"));

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Date today = new Date(date.getYear() - 1900, date.getMonth(), date.getDay());
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
                Date tomorrow = c.getTime();

                calendarlist.clear();
                calendarlist.addAll(dBhelper.getcalendarDaylist(today, tomorrow));
                recyclerAdapter.notifyDataSetChanged();
            }
        });
/*
        calendarView.setOnDateChangedListener(new MaterialCalendarView({
            public void onSelectedDayChange(CalendarView view, int year, int month,int dayOfMonth) {

            }
        });*/
    }

    //从New_Calendar_Activity返回时，刷新界面
    @Override
    protected void onResume(){
        super.onResume();
        calendarlist.clear();
        calendarlist.addAll(dBhelper.getcalendarlist());
        recyclerAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
}
