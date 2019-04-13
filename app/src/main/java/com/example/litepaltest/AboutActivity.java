package com.example.litepaltest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.githang.statusbar.StatusBarCompat;

/**
 * Created by Administrator on 2018/3/24.
 */

public class AboutActivity extends AppCompatActivity {
    private Button back;

    private String[] data ={"grace19980919","e18321929826","BIGBANGISVIP980912"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                AboutActivity.this,android.R.layout.simple_list_item_1,data
        );
        ListView listView=(ListView) findViewById(R.id.about_list);
        listView.setAdapter(adapter);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"),true );

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        //点击返回按钮
        backListener();
    }

    //对“返回”按钮的相应
    private void backListener() {
        back = (Button) findViewById(R.id.title_about_back);

        //对"返回"按钮的响应，跳转至个人中心页面
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
