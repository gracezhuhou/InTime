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
 * Created by Administrator on 2018/3/23.
 */

public class Analysis_Setting_Activity extends AppCompatActivity {
    private Button back;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_setting);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"),true );

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        //点击返回按钮
        backListener();

        //点击确认按钮
        submitListener();
    }

    //对“返回”按钮的相应
    private void backListener() {
        back = (Button) findViewById(R.id.all_setting_back);

        //对"返回"按钮的响应，跳转至分析页面
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.todolist);
                //Intent intent = new Intent(Analysis_Setting_Activity.this, AnalysisActivity.class);
                //startActivity(intent);
                finish();
            }
        });
    }

    //对“提交”按钮的相应
    private void submitListener() {
        submit = (Button) findViewById(R.id.all_setting_submit);

        //对"提交"按钮的响应，跳转至分析页面
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.todolist);
                //Intent intent = new Intent(Analysis_Setting_Activity.this, AnalysisActivity.class);
                //startActivity(intent);
                finish();
            }
        });
    }

}
