package com.example.litepaltest;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2018/2/25.
 */

public class TimeRecyclerAdapter extends RecyclerView.Adapter<TimeRecyclerAdapter.ViewHolder>{
    private List<InTimeList> mTimeList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView timeItem;
        private Button btn_Delete;
        private CardView cardView;
        private ObservableScrollView scrollView;
        private TextView timeid;

        public ViewHolder(View view){
            super(view);
            timeItem = (TextView)view.findViewById(R.id.time_Item);
            btn_Delete = (Button) view.findViewById(R.id.delete);
            cardView = (CardView) view.findViewById(R.id.time_item_cardview);
            scrollView = (ObservableScrollView) view.findViewById(R.id.time_HorizontalScrollView);
            timeid = (TextView)view.findViewById(R.id.time_item_id);
        }
    }

    public TimeRecyclerAdapter(List<InTimeList> timeList){
        mTimeList = timeList;
    }

    @Override
    public TimeRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_item, parent, false);
        return new TimeRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){
        InTimeList time = mTimeList.get(position);
        holder.timeItem.setText(time.getName());
        holder.timeid.setText(String.valueOf(time.getId()));

        //设置内容布局的宽为屏幕宽度
        holder.cardView.getLayoutParams().width = ScreenUtil.getScreenWidth(holder.itemView.getContext()) - 325;
        holder.scrollView.scrollTo(0,0);    //关闭菜单

        //长按事件监听，长按开启修改页面
        holder.timeItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override///////////////////////////////////////////////////////////////////////////////////////
            public boolean onLongClick(View v) {
                String id = holder.timeid.getText().toString();
                Intent intent = new Intent(v.getContext(), New_Time_Activity.class);
                intent.putExtra("id_extra", id);
                v.getContext().startActivity(intent);
                return true;
            }
        });

        //点击删除按钮
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBhelper dBhelper = new DBhelper();
                InTimeList time = mTimeList.get(holder.getAdapterPosition());
                dBhelper.deleteTime(time.getStartTime());

                mTimeList.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
                holder.scrollView.scrollTo(0,0);    //关闭菜单
            }
        });

        //点击左边text，删除键自动缩回
        holder.timeItem.setOnClickListener(new View.OnClickListener() {
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
        return mTimeList.size();
    }
}

