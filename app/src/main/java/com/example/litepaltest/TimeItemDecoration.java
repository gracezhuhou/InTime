package com.example.litepaltest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2018/3/3.
 */

public class TimeItemDecoration  extends RecyclerView.ItemDecoration{
    // 写右边字的画笔(具体信息)
    private Paint mPaint;

    // 写左边日期字的画笔( 时间 + 日期)
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private Paint mPaint4;

    // 左 上偏移长度
    private int itemView_leftinterval;
    private int itemView_topinterval;

    // 轴点半径
    private int circle_radius;

    // 图标
    private Bitmap mIcon;


    // 在构造函数里进行绘制的初始化，如画笔属性设置等
    public TimeItemDecoration(Context context) {

        // 轴点画笔(红色)
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#66ff99"));

        // 左边时间文本画笔(蓝色)
        // 此处设置了两只分别设置 时分 & 年月
        mPaint1 = new Paint();
        mPaint1.setColor(Color.GRAY);
        mPaint1.setTextSize(35);

        mPaint2 = new Paint();
        mPaint2.setColor(Color.GRAY);
        mPaint2.setTextSize(35);

        mPaint3 = new Paint();
        mPaint3.setColor(Color.parseColor("#9C9C9C"));
        mPaint3.setTextSize(38);

        mPaint4 = new Paint();
        mPaint4.setColor(Color.parseColor("#9C9C9C"));
        mPaint4.setTextSize(38);


        // 赋值ItemView的左偏移长度
        itemView_leftinterval = 300;

        // 赋值ItemView的上偏移长度
        itemView_topinterval = 50;

        // 赋值轴点圆的半径为10
        circle_radius = 10;

        // 获取图标资源
        // mIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);

    }

    // 重写getItemOffsets（）方法
    // 作用：设置ItemView 左 & 上偏移长度
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // 设置ItemView的左 & 上偏移长度分别为200 px & 50px,即此为onDraw()可绘制的区域
        outRect.set(itemView_leftinterval, itemView_topinterval, 0, 0);

    }

    // 重写onDraw（）
    // 作用:在间隔区域里绘制时光轴线 & 时间文本
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        // 获取RecyclerView的Child view的个数
        int childCount = parent.getChildCount();

        // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
        for (int i = 0; i < childCount; i++) {

            // 获取每个Item对象
            View child = parent.getChildAt(i);

            /**
             * 绘制轴点
             */
            // 轴点 = 圆 = 圆心(x,y)
            float centerx = child.getLeft() - itemView_leftinterval / 3;
            float centery = child.getTop(); //- itemView_topinterval + (itemView_topinterval + child.getHeight()) / 2;
            // 绘制轴点圆
            c.drawCircle(centerx, centery, circle_radius, mPaint);
            c.drawCircle(centerx, centery +  child.getHeight(), circle_radius, mPaint);
            // 通过Canvas绘制角标
            // c.drawBitmap(mIcon,centerx - circle_radius ,centery - circle_radius,mPaint);

            /**
             * 绘制上半轴线
             */
            // 上端点坐标(x,y)
            float upLine_up_x = centerx;
            float upLine_up_y = child.getTop() - itemView_topinterval ;

            // 下端点坐标(x,y)
            float upLine_bottom_x = centerx;
            float upLine_bottom_y = centery - circle_radius;

            //绘制上半部轴线
            c.drawLine(upLine_up_x, upLine_up_y, upLine_bottom_x, upLine_bottom_y, mPaint);

            /**
             * 绘制下半轴线
             */
            // 上端点坐标(x,y)
            float bottomLine_up_x = centerx;
            float bottom_up_y = centery + circle_radius;

            // 下端点坐标(x,y)
            float bottomLine_bottom_x = centerx;
            float bottomLine_bottom_y = child.getBottom();

            //绘制下半部轴线
            c.drawLine(bottomLine_up_x, bottom_up_y, bottomLine_bottom_x, bottomLine_bottom_y, mPaint);


            /**
             * 绘制左边时间文本
             */
            // 获取每个Item的位置
            int index = parent.getChildAdapterPosition(child);
            // 设置文本起始坐标
            float Text_x = child.getLeft() - itemView_leftinterval * 5 / 6;
            float Text_y = centery;//upLine_bottom_y;

            TextView timeID = (TextView) child.findViewById(R.id.time_item_id);
            String ID = timeID.getText().toString();
            DBhelper dBhelper = new DBhelper();
            InTimeList time = dBhelper.findTimeById(ID);
            Date startTime = time.getStartTime();
            Date endTime = time.getEndTime();
            SimpleDateFormat formatter_HHmm = new SimpleDateFormat("HH:mm");
            SimpleDateFormat formatter_yyMMdd = new SimpleDateFormat("yy-MM-dd");
            SimpleDateFormat formatter_MMdd = new SimpleDateFormat("MM-dd");

            // 根据Item位置设置时间文本
            if (startTime.getYear() != endTime.getYear()){
                c.drawText(formatter_yyMMdd.format(endTime), 10, Text_y + child.getHeight() - 20, mPaint3);
                c.drawText(formatter_HHmm.format(startTime), Text_x, Text_y + 35, mPaint1);
                c.drawText(formatter_HHmm.format(endTime), Text_x, Text_y + child.getHeight() + 15, mPaint2);
            }
            else if (startTime.getDay() != endTime.getDay() || startTime.getMonth() != endTime.getMonth()){
                c.drawText(formatter_MMdd.format(endTime), 10, Text_y + child.getHeight() - 20, mPaint3);
                c.drawText(formatter_HHmm.format(startTime), Text_x, Text_y + 35, mPaint1);
                c.drawText(formatter_HHmm.format(endTime), Text_x, Text_y + child.getHeight() + 15, mPaint2);
            }
            else {
                c.drawText(formatter_HHmm.format(startTime), Text_x, Text_y + 35, mPaint1);
                c.drawText(formatter_HHmm.format(endTime), Text_x, Text_y + child.getHeight(), mPaint2);
            }

            if (index == 0) {
                c.drawText(formatter_yyMMdd.format(startTime), 10, Text_y, mPaint4);
            }
            else {
                View postchild = parent.getChildAt(index - 1);
                TextView postid = (TextView) postchild.findViewById(R.id.time_item_id);
                String postID = postid.getText().toString();
                InTimeList postTime = dBhelper.findTimeById(postID);
                if (postTime.getEndTime().getYear() != startTime.getYear()) {
                    c.drawText(formatter_yyMMdd.format(startTime), 10, Text_y, mPaint4);
                }
                else if (postTime.getEndTime().getMonth() != startTime.getMonth() || postTime.getEndTime().getDay() != startTime.getDay()) {
                    c.drawText(formatter_MMdd.format(startTime), 10, Text_y, mPaint4);
                }
            }
        }
    }

}



