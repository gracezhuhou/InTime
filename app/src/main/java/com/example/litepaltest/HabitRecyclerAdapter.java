package com.example.litepaltest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by lenovo on 2018/2/22.
 */

public class HabitRecyclerAdapter extends RecyclerView.Adapter<HabitRecyclerAdapter.ViewHolder>{
    private List<HabitList> mHabitList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox habitItem;
        ProgressBar progressBar;
        private Button btn_Delete;
        private CardView cardView;
        private ObservableScrollView scrollView;

        public ViewHolder(View view){
            super(view);
            habitItem = (CheckBox)view.findViewById(R.id.habit_Item);
            progressBar = (ProgressBar)view.findViewById(R.id.progress);
            btn_Delete = (Button) view.findViewById(R.id.delete);
            cardView = (CardView) view.findViewById(R.id.habit_Item_CardView);
            scrollView = (ObservableScrollView) view.findViewById(R.id.habit_HorizontalScrollView);
        }
    }

    public HabitRecyclerAdapter(List<HabitList> habitlist){
        mHabitList = habitlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        HabitList habit = mHabitList.get(position);
        holder.habitItem.setText(habit.getName());
        holder.progressBar.setProgress(habit.getDays());
        holder.habitItem.setChecked(habit.getToday());
        if (habit.getToday()){
            holder.habitItem.setPaintFlags(holder.habitItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //设置内容布局的宽为屏幕宽度
        holder.cardView.getLayoutParams().width = ScreenUtil.getScreenWidth(holder.itemView.getContext()) - 50;
        holder.scrollView.scrollTo(0,0);    //关闭菜单

        holder.habitItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String name = buttonView.getText().toString();
                DBhelper dBhelper = new DBhelper();

                if (isChecked) {
                    holder.progressBar.setProgress(dBhelper.addDay(name, true));
                    holder.habitItem.setPaintFlags(holder.habitItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else {
                    holder.progressBar.setProgress(dBhelper.addDay(name, false));
                    holder.habitItem.setPaintFlags(holder.habitItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });

        //点击删除按钮
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.habitItem.getText().toString();
                mHabitList.remove(position);
                notifyDataSetChanged();

                DBhelper dBhelper = new DBhelper();
                dBhelper.deleteHabit(name);
                holder.scrollView.scrollTo(0,0);    //关闭菜单
            }
        });

        //点击左边text，删除键自动缩回
        holder.habitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.scrollView.scrollTo(0,0);    //关闭菜单

            }
        });

        holder.scrollView.setScrollListener(new ObservableScrollView.ScrollListener() {
            @Override
            public void scrollOritention(int l, int t, int oldl, int oldt) {
                //滑动数据已经接收
                //l = x, t = y

                //左划
                if (l - oldl > 1) {
                    holder.scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                    return;
                }
                //右划
                if (oldl - l > 1) {
                    holder.scrollView.smoothScrollTo(0, 0);    //关闭菜单
                }
            }
        });

    }

    @Override
    public int getItemCount(){
        return mHabitList.size();
    }
}
