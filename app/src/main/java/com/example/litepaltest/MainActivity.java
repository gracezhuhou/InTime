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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.githang.statusbar.StatusBarCompat;

import java.io.ByteArrayInputStream;
import java.util.List;

public class MainActivity extends BaseActivity {

    private Button add_ToDo;
    private CircleImageView pic;
    private RecyclerView todoRecylerView;
    private ToDoRecyclerAdapter recyclerAdapter;
    private List<ToDoList> todolist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todolist);      //暂且默认首页面是待办事项

        //android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //点击底部按钮
        bottomListener();

        //点击左上图片
        picListener();

        //初始化界面，载入已建立待办事项
        initToDoList();

        //点击“+”键
        addListener();
    }

    //对"+"按钮的响应
    private void addListener(){
        //跳转至增加待办事项页面
        addToDo();
    }

    //对底部按钮的响应
    private void bottomListener(){
        ToCalendar = (Button) findViewById(R.id.button_Calendar);
        ToTime = (Button) findViewById(R.id.button_Time);
        ToHabit = (Button) findViewById(R.id.button_Habit);
        ToAnalysis = (Button) findViewById(R.id.button_Analysis);
        ToToDo = (Button) findViewById(R.id.button_ToDo);

        ToToDo.setBackgroundColor(Color.parseColor("#EE82EE"));

        //对"日程"按钮的响应，跳转至日程页面
        ToCalendar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"时间轴"按钮的响应，跳转至时间轴页面
        ToTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, TimeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"习惯"按钮的响应，跳转至习惯页面
        ToHabit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, HabitActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"分析"按钮的响应，跳转至分析页面
        ToAnalysis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //对头像图片的响应
    void picListener(){
        pic = (CircleImageView) findViewById(R.id.profile);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
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

    //增加待办事项
    private void addToDo(){
        todoRecylerView = (RecyclerView)findViewById(R.id.todo_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        todoRecylerView.setLayoutManager(layoutManager);
        recyclerAdapter = new ToDoRecyclerAdapter(todolist);
        todoRecylerView.setAdapter(recyclerAdapter);
        todoRecylerView.setHasFixedSize(true);
        //add_ToDo = (Button) findViewById(R.id.title_todo).findViewById(R.id.title_add);
        add_ToDo = (Button)findViewById(R.id.title_add);

        add_ToDo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, New_ToDo_Activity.class);
                startActivity(intent);
            }
        });
    }

    //初始化待办事项
    private void initToDoList(){
        todolist = dBhelper.gettodolist();
    }

    //从New_ToDo_Activity返回时，刷新界面
    @Override
    protected void onResume(){
        super.onResume();
        todolist.clear();
        todolist.addAll(dBhelper.gettodolist());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //提示
    }
}
