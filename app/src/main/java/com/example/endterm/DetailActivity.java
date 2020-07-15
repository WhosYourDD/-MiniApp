package com.example.endterm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.endterm.func.AppInfo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DetailActivity extends AppCompatActivity {

    ImageView icon;
    TextView tv_name;
    TextView tv_today;
    TextView tv_week;
    LineChart chart;
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String pname = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        toolbar = findViewById(R.id.toolbar_detail);
        toolbar.setTitle("详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        icon = findViewById(R.id.App_icon);
        tv_name = findViewById(R.id.App_name);
        tv_today = findViewById(R.id.today_time);
        tv_week = findViewById(R.id.week_time);
        chart = findViewById(R.id.detail_chart);
        AppInfo appInfo = new AppInfo(getApplicationContext(), pname);
        icon.setImageDrawable(appInfo.getAppicon());
        tv_name.setText(appInfo.getAppname());
        List<Integer> data = getUseInfo(pname);
        tv_today.setText(helper(data.get(data.size()-1)));
        int weekTime = 0;
        for (int elem:data){
            weekTime+=elem;
        }
        tv_week.setText(helper(weekTime));
        drawChart(data);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public List<Integer> getUseInfo(String packagename){
        List<Integer> res = new ArrayList<>();
        // 获取时间
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        int y = c1.get(Calendar.YEAR);
        int m = c1.get(Calendar.MONTH);
        int d = c1.get(Calendar.DATE);
        c2.set(y, m, d, 0, 0, 0);
        List<Long> calendars = new ArrayList<>();
        calendars.add(c1.getTimeInMillis());
        calendars.add(c2.getTimeInMillis());
        for (int i=1; i<7; i++){
            calendars.add(c2.getTimeInMillis()-i*24 * 60 * 60 * 1000);
        }
        // 获取每个时间段的使用时间
        UsageStatsManager usm = (UsageStatsManager)getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Collections.reverse(calendars);
        for(int i=0;i<7;i++){
            Long end = calendars.get(i+1);
            Long start = calendars.get(i);
            int sum_minute = 0;
            List<UsageStats> USlist = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end);
            for(UsageStats elem:USlist){
                if (elem.getPackageName().equals(packagename)) {
                    long elem_time = elem.getTotalTimeInForeground();
                    long temp = TimeUnit.MILLISECONDS.toMinutes(elem_time);
                    sum_minute += (int) temp;
                }
            }
            res.add(sum_minute);
        }
        return res;
    }

    public void drawChart(List<Integer> week_data){
        List<Entry> values = new ArrayList<>();
        for(int i=0;i<week_data.size();i++){
            int elem = week_data.get(i);
            values.add(new Entry(i+1, elem));
        }
        LineDataSet dataSet = new LineDataSet(values, "应用七日使用情况");
        dataSet.setCircleColor(Color.parseColor("#4267B1"));
        LineData data =  new LineData(dataSet);
        data.setDrawValues(false);
        chart.getDescription().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.parseColor("#555555"));
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int v = (int) value;
                if (v==7)return "今天";
                Date today = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                // 7 1 2 3 4 5 6
                // 1 2 3 4 5 6 7
                int weekday = c.get(Calendar.DAY_OF_WEEK)-1;
                if (weekday==0)weekday=7;
                int cur = weekday-7+v;
                if (cur<=0)cur+=7;
                return "周"+cur;
            }
        });
        chart.getAxisLeft().setEnabled(false);
        YAxis yAxis = chart.getAxisRight();
        yAxis.setTextSize(12);
        yAxis.setTextColor(Color.parseColor("#999999"));
        yAxis.setDrawAxisLine(false);
        yAxis.setGridColor(Color.parseColor("#DDDDDD"));
        yAxis.setGridLineWidth(0.5f);
        yAxis.setLabelCount(4);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int v = (int) value;
                int h = v/60;
                int m = v%60;
                String t = "";
                if (h>0)t+=h+" h";
                if (m>0)t+=m+" m";
                return t;
            }
        });
        chart.setData(data);
    }
    private String helper(int x){
        String re = "";
        long hour = x/60;
        long minute = x%60;
        if(hour>0){
            if (minute>0) re = hour+"时"+minute+"分";
            else re=hour+"时";
        }else if (minute>0){
            re = minute+"分";
        }else{
            re = "<1分钟>";
        }
        return re;
    }
}
