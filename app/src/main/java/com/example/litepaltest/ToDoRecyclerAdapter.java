package com.example.litepaltest;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by lenovo on 2018/2/23.
 */

public class ToDoRecyclerAdapter extends RecyclerView.Adapter<ToDoRecyclerAdapter.ViewHolder> {
    private List<ToDoList> mToDoList;
    private DBhelper dBhelper = new DBhelper();

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView todoItem;
        private TextView deadline;
        private Button btn_Delete;
        private CardView cardView;
        private ObservableScrollView scrollView;
        private SeekBar bar;
        private Button btn_play;

        public ViewHolder(View view) {
            super(view);
            todoItem = (TextView) view.findViewById(R.id.todo_Item);
            deadline = (TextView) view.findViewById(R.id.todo_Item_ddl);
            btn_Delete = (Button) view.findViewById(R.id.delete);
            cardView = (CardView) view.findViewById(R.id.todo_cardview);
            scrollView = (ObservableScrollView) view.findViewById(R.id.todo_HorizontalScrollView);
            bar = (SeekBar) view.findViewById(R.id.todo_bar);
            btn_play = (Button) view.findViewById(R.id.start);
        }
    }

    public ToDoRecyclerAdapter(List<ToDoList> todoList) {
        mToDoList = todoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ToDoList todo = mToDoList.get(position);
        holder.todoItem.setText(todo.getName());
        if(todo.getIsfinished()){
            holder.todoItem.setPaintFlags(holder.todoItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.deadline.setPaintFlags(holder.deadline.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.btn_play.setVisibility(View.INVISIBLE);
            holder.bar.setProgress(100);
            holder.deadline.getPaint().setFakeBoldText(false);
        }
        else{
            holder.bar.setProgress(0);
            holder.todoItem.setPaintFlags(holder.todoItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.deadline.setPaintFlags(holder.deadline.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.btn_play.setVisibility(View.VISIBLE);
        }

        if (!todo.getIsRecording()) {
            holder.btn_play.setBackgroundResource(R.drawable.play2);
        }
        else {
            holder.btn_play.setBackgroundResource(R.drawable.end2);
        }

        //设置内容布局的宽为屏幕宽度
        holder.cardView.getLayoutParams().width = ScreenUtil.getScreenWidth(holder.itemView.getContext()) - 100;
        holder.scrollView.scrollTo(0, 0);    //关闭菜单

        Date deadline = todo.getDeadline();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String ddltext = "截止于" + formatter.format(deadline);
        holder.deadline.setText(ddltext);

        Date now = new Date(System.currentTimeMillis());
        if (deadline.compareTo(now) != 1){
            holder.deadline.getPaint().setFakeBoldText(true);
        }
        if (deadline.compareTo(now) != 1){
            //holder.deadline.setTextColor(Color.parseColor("#EE6AA7"));
        }


        //长按事件监听，长按开启修改页面
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String name = holder.todoItem.getText().toString();
                Intent intent = new Intent(v.getContext(), New_ToDo_Activity.class);
                intent.putExtra("name_extra", name);
                v.getContext().startActivity(intent);
                return true;
            }
        });

        //点击删除按钮
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.todoItem.getText().toString();
                mToDoList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());

                dBhelper.deleteToDoList(name);
                holder.scrollView.scrollTo(0, 0);    //关闭菜单
            }
        });

        //点击左边卡片，删除键自动缩回
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.scrollView.scrollTo(0, 0);    //关闭菜单
            }
        });

        holder.bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int start;
            int end;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                start = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                end = seekBar.getProgress();
                String name = holder.todoItem.getText().toString();

                if (end == start) return;

                if (end - start > 30 || ( start > end && start - end < 30)) {
                    dBhelper.toDoFinished(name, true);

                    seekBar.setProgress(100);
                    holder.deadline.getPaint().setFakeBoldText(false);
                    holder.todoItem.setPaintFlags(holder.todoItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.deadline.setPaintFlags(holder.deadline.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.btn_play.setVisibility(View.INVISIBLE);

                    mToDoList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    mToDoList.add(dBhelper.findToDo(name));
                    notifyItemInserted(mToDoList.size() - 1);
                }
                else {
                    dBhelper.toDoFinished(name, false);

                    seekBar.setProgress(0);
                    holder.todoItem.setPaintFlags(holder.todoItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.deadline.setPaintFlags(holder.deadline.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.btn_play.setVisibility(View.VISIBLE);

                    mToDoList.clear();
                    mToDoList.addAll(dBhelper.gettodolist());
                    notifyDataSetChanged();
                }
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

        holder.bar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                holder.scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        holder.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = v.getContext().getSharedPreferences("Recording",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.apply();

                Boolean isRecording = pref.getBoolean("isRecording", false);
                Boolean isAuto = pref.getBoolean("isAuto", false);
                Boolean isToDoRecording = pref.getBoolean("isToDoRecording", false);
                ToDoList todo = mToDoList.get(holder.getAdapterPosition());

                if (isRecording  && !isAuto){
                    Toast.makeText(v.getContext(), "时间轴记录中", Toast.LENGTH_SHORT).show();
                }
                else if (isToDoRecording && !todo.getName().equals(pref.getString("ToDoName",""))){
                    Toast.makeText(v.getContext(), "其他待办事项记录中", Toast.LENGTH_SHORT).show();
                }
                else if (!isToDoRecording || isAuto) {
                    BaseActivity.timeNow = new Date(System.currentTimeMillis());
                    holder.btn_play.setBackgroundResource(R.drawable.end2);
                    //BaseActivity.isToDoRecording = true;
                    //BaseActivity.isAuto = false;

                    editor.putBoolean("isToDoRecording", true);
                    editor.putBoolean("isRecording", false);
                    editor.putBoolean("isAuto", false);
                    editor.putString("ToDoName", todo.getName());
                    editor.apply();

                    String name = holder.todoItem.getText().toString();
                    dBhelper.toDoRecording(name, true);
                }
                else{
                    BaseActivity.timeEnd = new Date(System.currentTimeMillis());
                    holder.btn_play.setBackgroundResource(R.drawable.play2);
                    //BaseActivity.isToDoRecording = false;
                    editor.putBoolean("isToDoRecording", false);
                    editor.apply();

                    String name = holder.todoItem.getText().toString();
                    dBhelper.toDoRecording(name, false);

                    //增加时间记录
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");

                    InTimeList time = dBhelper.addTime(todo.getName(), todo.getNote(), todo.getCategory(), BaseActivity.timeNow, BaseActivity.timeEnd, BaseActivity.longtiude, BaseActivity.latitude);
                    if (time == null) {
                        Toast.makeText(v.getContext(), "时间冲突啦", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return mToDoList.size();
    }
}
