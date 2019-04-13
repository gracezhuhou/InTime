package com.example.litepaltest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

public class HabitActivity extends BaseActivity {
    private Button add_Habit;

    private ImageView pic;
    private EditText newHabit;
    private String name;
    private RecyclerView habitRecylerView;
    private HabitRecyclerAdapter recyclerAdapter;
    private List<HabitList> habitlist;
    private CheckBox checkBox;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habitlist);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"),true );

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.hide();
        }

        //初始化界面，载入已建立习惯
        initHabitList();

        init();

        //点击底部按钮
        bottomListener();

        //点击左上图片
        picListener();

        //点击“+”键
        addHabit();
    }

    //对底部按钮的响应
    private void bottomListener(){
        //对"待办"按钮的响应，跳转至待办页面
        ToToDo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.todolist);
                Intent intent = new Intent(HabitActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"日程"按钮的响应，跳转至日程页面
        ToCalendar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.calendarlist);
                Intent intent = new Intent(HabitActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"时间轴"按钮的响应，跳转至时间轴页面
        ToTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.timelist);
                Intent intent = new Intent(HabitActivity.this, TimeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"分析"按钮的响应，跳转至分析页面
        ToAnalysis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //setContentView(R.layout.habitlist);
                Intent intent = new Intent(HabitActivity.this, AnalysisActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //对头像图片的响应
    private void picListener(){
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HabitActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    //对"+"按钮的响应
    private void addHabit(){
        //“+”监听
        add_Habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newHabit.requestFocus();
            }
        });

        //新习惯输入完成后
        newHabit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //焦点消失时，获取习惯名称
                    name = newHabit.getText().toString();
                    if (!name.equals("")) {
                        newHabit.setText("");
                        //添加习惯

                        //页面增加习惯
                        addHabitOnView(name);
                        newHabit.clearFocus();
                    }
                }
            }
        });

        //按下回车键，表示确定
        newHabit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    newHabit.clearFocus();
                }
                return false;
            }
        });
    }

    //在页面上添加新习惯
    private void addHabitOnView(String name){
        habitlist.add(dBhelper.addHabit(name));
        recyclerAdapter.notifyItemInserted(habitlist.size() - 1);
        habitRecylerView.scrollToPosition(habitlist.size() - 1);
    }

    //初始化习惯
    private void initHabitList(){
        habitlist = dBhelper.getHabitList();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }

    private void init(){
        newHabit = (EditText)findViewById(R.id.newhabit);
        newHabit.clearFocus();
        pref = getSharedPreferences("HabitDay", MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();

        Date today = new Date(System.currentTimeMillis());
        int day = pref.getInt("Today", -1);
        if (day == -1){
            editor.putInt("Today", today.getDate());
            editor.apply();
        }
        else if (day != today.getDate()){
            dBhelper.clearhabitday();
            editor.putInt("Today", today.getDate());
            editor.apply();
        }

        ToToDo = (Button) findViewById(R.id.button_ToDo);
        ToCalendar = (Button) findViewById(R.id.button_Calendar);
        ToTime = (Button) findViewById(R.id.button_Time);
        ToHabit = (Button) findViewById(R.id.button_Habit);
        ToAnalysis = (Button) findViewById(R.id.button_Analysis);
        ToHabit.setBackgroundColor(Color.parseColor("#ffff99"));

        add_Habit = (Button)findViewById(R.id.title_habit).findViewById(R.id.title_add);
        habitRecylerView = (RecyclerView)findViewById(R.id.habit_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        habitRecylerView.setLayoutManager(layoutManager);
        recyclerAdapter = new HabitRecyclerAdapter(habitlist);
        habitRecylerView.setAdapter(recyclerAdapter);
        habitRecylerView.setHasFixedSize(true);

        pic = (ImageView)findViewById(R.id.profile);

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }
}
