package com.example.litepaltest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by ASUS on 2018/3/24.
 */

public class RankingActivity extends AppCompatActivity{
    private Button back;
    private String[] data;
    private String str;
    private RecyclerView rankRecyslerview;
    private RankRecyclerAdapter recyclerAdapter;
    private List<MyUser> userlist = new ArrayList<MyUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rankinglist);

        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"), true);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        initId();

        //点击返回按钮
        backListener();

        Bmob.initialize(this, "75c88783b55ef9650c609d100ef036b4");

        loadData();
    }

    //返回时间排行
    private void loadData() {
        BmobQuery<MyUser> bmobQuery = new BmobQuery<MyUser>();
        bmobQuery.setLimit(30);
        bmobQuery.order("-StudyTime");
        bmobQuery.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    for (MyUser user : list) {
                        //Toast.makeText(RankingActivity.this,"查询成功：共"+list.size()+"条数据。",Toast.LENGTH_SHORT).show();

                        //获得UserName的信息,获得StudyTime信息
                        userlist.add(user);
                    }
                }
                else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());

                }

                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    //对“返回”按钮的相应
    private void backListener() {
        //对"返回"按钮的响应，跳转至个人中心页面
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initId(){
        back = (Button) findViewById(R.id.title_ranking_back);
        rankRecyslerview = (RecyclerView) findViewById(R.id.rank_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rankRecyslerview.setLayoutManager(layoutManager);
        recyclerAdapter = new RankRecyclerAdapter(userlist);
        rankRecyslerview.setAdapter(recyclerAdapter);
        rankRecyslerview.setHasFixedSize(true);
    }
}


