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

public class FriendActivity extends AppCompatActivity {

    private Button back;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"), true);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        //点击“返回”按钮
        backListener();

        //点击“+”按钮
        addListener();

    }

    //对“返回”按钮的响应
    private void backListener() {
        back = (Button) findViewById(R.id.title_friend_back);

        //对"返回"按钮的响应，跳转至个人中心页面
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(FriendActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //对“+”按钮的响应
    private void addListener() {
        add = (Button) findViewById(R.id.title_friend_add);

        //对"+"按钮的响应，跳转至添加好友页面
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(FriendActivity.this, New_Friend_Activity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
