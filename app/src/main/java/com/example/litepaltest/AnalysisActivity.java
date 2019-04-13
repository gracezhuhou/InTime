package com.example.litepaltest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class AnalysisActivity extends BaseActivity {
    private ImageView pic;
    //private Button set;
    private ImageButton left_Arrow;
    private ImageButton right_Arrow;
    private TextView timeRange;
    private String [] Month = {"January", "Fabuary", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private int [] endDay = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int year;
    private int month;

    private PieChartView pieChart;
    private ColumnChartView columnChart;
    private LineChartView lineChart;
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private String [] category = {"学习", "工作", "生活"};
    private int [] time = new int [3];
    private PieChartData pieChardata;
    private List<SliceValue> values = new ArrayList<SliceValue>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis);

        initID();

        //点击底部按钮
        bottomListener();

        //点击左上图片
        picListener();

        //月份
        TimeRangeListener();

        //点击“设置”键
        //settingListener();

        //图标设置
        setChart();
    }

    //对底部按钮的响应
    private void bottomListener() {
        //对"待办"按钮的响应，跳转至日程页面
        ToToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.todolist);
                Intent intent = new Intent(AnalysisActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"日程"按钮的响应，跳转至日程页面
        ToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.calendarlist);
                Intent intent = new Intent(AnalysisActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"时间轴"按钮的响应，跳转至时间轴页面
        ToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.timelist);
                Intent intent = new Intent(AnalysisActivity.this, TimeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //对"习惯"按钮的响应，跳转至习惯页面
        ToHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.habitlist);
                Intent intent = new Intent(AnalysisActivity.this, HabitActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //对头像图片的响应
    private void picListener() {
        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnalysisActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    //时间范围
    private void TimeRangeListener(){
        left_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time;
                if (month == 0){
                    year--;
                    month = 11;
                    time = Month[11] + " " + Integer.toString(year);
                }
                else {
                    time = Month[--month] + " " + Integer.toString(year);
                }
                timeRange.setText(time);

                rePaint();

            }
        });

        right_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time;
                if (month == 11){
                    year++;
                    month = 0;
                    time = Month[0] + " " + Integer.toString(year);
                }
                else {
                    time = Month[++month] + " " + Integer.toString(year);
                }
                timeRange.setText(time);

                rePaint();
            }
        });
    }
/*
    //设置
    private void settingListener(){
    set.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(AnalysisActivity.this, Analysis_Setting_Activity.class);
            startActivity(intent);
        }
    });
    }
*/
    //图表
    private void setChart() {
        initLineChart();//初始化

        pieChart.setOnValueTouchListener(selectListener);//设置点击事件监听
        initPieChart();
    }


    //LineChart
    //设置X 轴的显示
    private void getAxisXLables() {
        for (int i = 0; i < category.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(category[i]));
        }
    }

    //图表的每个点的显示
    private void getAxisPoints() {
        //time[0] = dBhelper.getCategoryTimeSum("学习") ;
        //time[1] = dBhelper.getCategoryTimeSum("工作") ;
        //time[2] = dBhelper.getCategoryTimeSum("生活") ;
        Date startMonth = new Date(year - 1900, month, 1, 0, 0, 0);
        Date endMonth;
        if (month == 1 && ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)){
            endMonth = new Date(year - 1900, month, 29, 0, 0, 0);
        }
        else {
            endMonth = new Date(year - 1900, month, endDay[month - 1], 23, 59, 59);
        }
        time[0] = dBhelper.getCategoryPeriodTimeSum("学习", startMonth, endMonth) ;
        time[1] = dBhelper.getCategoryPeriodTimeSum("工作", startMonth, endMonth) ;
        time[2] = dBhelper.getCategoryPeriodTimeSum("生活", startMonth, endMonth) ;

        for (int i = 0; i < time.length; i++) {
            mPointValues.add(new PointValue(i, time[i]));
        }
    }

    private void initLineChart() {
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点

        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setName("类别");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(10); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("总时间");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 3;
        lineChart.setCurrentViewport(v);*/
    }


    //PieChart
    // 获取数据
    private void setPieChartData(){
        for (int i = 0; i < time.length; ++i) {
            SliceValue sliceValue = new SliceValue((float) time[i], Color.parseColor("#FFCD41"));//这里的颜色是我写了一个工具类 是随机选择颜色的
            values.add(sliceValue);
            double per = (double)time[i]/(double)(time[0] + time[1] + time[2]) * 100;
            DecimalFormat df = new DecimalFormat("#.00");
            String percent = df.format(per);
            sliceValue.setLabel(category[i] + percent + "%");
        }
    }

    private void initPieChart() {
        setPieChartData();

        pieChardata = new PieChartData();
        pieChardata.setHasLabels(true);//显示表情
        pieChardata.setHasLabelsOnlyForSelected(false);//不用点击显示占的百分比
        pieChardata.setHasLabelsOutside(false);//占的百分比是否显示在饼图外面
        pieChardata.setHasCenterCircle(false);//是否是环形显示
        pieChardata.setValues(values);//填充数据
        pieChardata.setCenterCircleColor(Color.WHITE);//设置环形中间的颜色
        pieChardata.setCenterCircleScale(0.5f);//设置环形的大小级别
        //pieChardata.setCenterText1("饼图测试");//环形中间的文字1
        pieChardata.setCenterText1Color(Color.BLACK);//文字颜色
        pieChardata.setCenterText1FontSize(14);//文字大小

        //pieChardata.setCenterText2("饼图测试");
        pieChardata.setCenterText2Color(Color.BLACK);
        pieChardata.setCenterText2FontSize(18);
        /**这里也可以自定义你的字体   Roboto-Italic.ttf这个就是你的字体库*/
//      Typeface tf = Typeface.createFromAsset(this.getAssets(), "Roboto-Italic.ttf");
//      data.setCenterText1Typeface(tf);

        pieChart.setPieChartData(pieChardata);
        pieChart.setValueSelectionEnabled(true);//选择饼图某一块变大
        pieChart.setAlpha(0.9f);//设置透明度
        pieChart.setCircleFillRatio(1f);//设置饼图大小

    }

    //监听事件
    private PieChartOnValueSelectListener selectListener = new PieChartOnValueSelectListener() {

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onValueSelected(int arg0, SliceValue value) {
            // TODO Auto-generated method stub
            Toast.makeText(AnalysisActivity.this, "Selected: " + value.getValue(), Toast.LENGTH_SHORT).show();
        }
    };

    public void rePaint(){
        mPointValues.clear();
        initLineChart();
        values.clear();
        initPieChart();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", null);
        if (temp != null){
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }

    private void initID(){
        // et = (Button)findViewById(R.id.analysis_set);
        pic = (ImageView) findViewById(R.id.profile);
        ToToDo = (Button) findViewById(R.id.button_ToDo);
        ToCalendar = (Button) findViewById(R.id.button_Calendar);
        ToTime = (Button) findViewById(R.id.button_Time);
        ToHabit = (Button) findViewById(R.id.button_Habit);
        ToAnalysis = (Button) findViewById(R.id.button_Analysis);
        ToAnalysis.setBackgroundColor(Color.parseColor("#ffcc66"));

        right_Arrow = (ImageButton)findViewById(R.id.rightArrow);
        left_Arrow = (ImageButton)findViewById(R.id.leftArrow);
        timeRange = (TextView)findViewById(R.id.date);
        Date today = new Date(System.currentTimeMillis());
        year = today.getYear() + 1900;
        month = today.getMonth();
        String time = Month[month] + " " + Integer.toString(year);
        timeRange.setText(time);

        pieChart = (PieChartView) findViewById(R.id.pie_chart);
        lineChart = (LineChartView) findViewById(R.id.line_chart);
        //columnChart = (ColumnChartView) findViewById(R.id.colume_chart);

        SharedPreferences sharedPreferences=getSharedPreferences("picture",MODE_PRIVATE);
        String temp = sharedPreferences.getString("pic", "");
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        if (Drawable.createFromStream(bais, "") != null) {
            pic.setImageDrawable(Drawable.createFromStream(bais, ""));
        }
    }
}