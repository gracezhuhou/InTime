package com.example.litepaltest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.githang.statusbar.StatusBarCompat;

/**
 * Created by Administrator on 2018/3/20.
 */

public class New_Friend_Activity extends AppCompatActivity {

    private Button back;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_friend);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"), true);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        //点击“返回”按钮
        backListener();

        //点击“确认”按钮
        submitListener();

    }

    //对“返回”按钮的响应
    private void backListener() {
        back = (Button) findViewById(R.id.title_new_friend_back);

        //对"返回"按钮的响应，跳转至好友页面
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(New_Friend_Activity.this, FriendActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //对“确认”按钮的响应
    private void submitListener() {
        submit = (Button) findViewById(R.id.title_new_friend_submit);

        //对"返回"按钮的响应，跳转至好友页面
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(New_Friend_Activity.this, FriendActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
