package com.example.litepaltest;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ASUS on 2018/3/24.
 */

public class RankRecyclerAdapter extends RecyclerView.Adapter<RankRecyclerAdapter.ViewHolder>{
    private List<MyUser> myUsers;

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView friendItem;
        //private CardView cardView;
        private TextView timeSum;
        private TextView icon;


        public ViewHolder(View view){
            super(view);
            friendItem = (TextView)view.findViewById(R.id.friend_Item);
            //cardView = (CardView) view.findViewById(R.id.friend_Item_CardView);
            timeSum = (TextView) view.findViewById(R.id.time_sum);
            icon = (TextView) view.findViewById(R.id.rankIC);
        }
    }

    public RankRecyclerAdapter(List<MyUser> users){
        myUsers = users;
    }

    @Override
    public RankRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new RankRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        MyUser user = myUsers.get(position);
        String name = "用户名：" + user.getUsername();
        String time = "学习时长：" + Integer.toString(user.getStudyTime());
        holder.friendItem.setText(name);
        holder.timeSum.setText(time);
        String rank = Integer.toString(position + 1);
        holder.icon.setText(rank);
    }

    @Override
    public int getItemCount(){
        return myUsers.size();
    }
}

