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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2018/2/23.
 */

public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.ViewHolder>{
private List<CalendarList> mCalendarList;

static class ViewHolder extends RecyclerView.ViewHolder{
    TextView calendarItem;
    private TextView time;
    private Button btn_Delete;
    private CardView cardView;
    private ObservableScrollView scrollView;

    public ViewHolder(View view){
        super(view);
        calendarItem = (TextView)view.findViewById(R.id.calendar_Item);
        time = (TextView) view.findViewById(R.id.calendar_Item_time);
        btn_Delete = (Button) view.findViewById(R.id.delete);
        cardView = (CardView) view.findViewById(R.id.calendar_cardview);
        scrollView = (ObservableScrollView) view.findViewById(R.id.calendar_HorizontalScrollView);
    }
}

    public CalendarRecyclerAdapter(List<CalendarList> calendarList){
        mCalendarList = calendarList;
    }

    @Override
    public CalendarRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        return new CalendarRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        CalendarList calendar = mCalendarList.get(position);
        holder.scrollView.scrollTo(0,0);    //关闭菜单

        //设置内容布局的宽为屏幕宽度
        holder.cardView.getLayoutParams().width = ScreenUtil.getScreenWidth(holder.itemView.getContext()) - 100;

        Date startTime = calendar.getStartTime();
        Date endTime = calendar.getEndTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String timetext;
        String start = formatter.format(startTime);
        String end = formatter.format(endTime);
        if (start.equals(end)){
            timetext = start;
        }
        else {
            timetext = start + "~" + end;
        }
        holder.time.setText(timetext);

        SimpleDateFormat formatterhh = new SimpleDateFormat("HH:mm");
        String starthh = formatterhh.format(startTime);
        String endhh = formatterhh.format(endTime);
        String text = calendar.getName() + " " + starthh + "~" + endhh;
        holder.calendarItem.setText(text);


        //长按事件监听，长按开启修改页面
        holder.calendarItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String duringTime = holder.time.getText().toString();
                String [] start = duringTime.split("~");
                String nametext = holder.calendarItem.getText().toString();
                String [] name = nametext.split(" ");

                Intent intent = new Intent(v.getContext(), New_Calendar_Activity.class);
                intent.putExtra("startTime_extra", start[0]);
                intent.putExtra("name_extra", name[0]);
                v.getContext().startActivity(intent);
                return true;
            }
        });

        //点击删除按钮
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.calendarItem.getText().toString();

                mCalendarList.remove(position);
                notifyItemRemoved(position);

                DBhelper dBhelper = new DBhelper();
                dBhelper.deleteCalendarList(name);
            }
        });

        //点击左边text，删除键自动缩回
        holder.calendarItem.setOnClickListener(new View.OnClickListener() {
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
        return mCalendarList.size();
    }
}

