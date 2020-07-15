package com.example.endterm.navigation;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.endterm.R;
import com.example.endterm.adapters.adapter_ana;
import com.example.endterm.func.AppInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class ana extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TabLayout tablayout;
    private View root;
    private TextView tv1;
    private TextView tv_usetime; // 使用了36分钟
    private TextView tv_percent; // %的时间在屏幕上
    private ProgressBar pb; // 百分比
    private RecyclerView recyclerView;
    private LinearLayout ana_today; // while tab change hide ana_today or ana_week
    private BarChart ana_week;

    public ana() {

    }


    public static ana newInstance(String param1, String param2) {
        ana fragment = new ana();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ana, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tablayout = getActivity().findViewById(R.id.tablayout);
        ana_today = getActivity().findViewById(R.id.ana_today);
        setBgRadius(ana_today, 20);
        ana_week = getActivity().findViewById(R.id.ana_week);
        setBgRadius(ana_week, 20);
        ana_week.setVisibility(View.GONE);
        recyclerView = getActivity().findViewById(R.id.recycleview_ana);
        setBgRadius(recyclerView, 20);
        tv_usetime = getActivity().findViewById(R.id.ana_today_usetime);
        tv_percent = getActivity().findViewById(R.id.ana_today_percent);
        pb = getActivity().findViewById(R.id.ana_today_pb);
        setRecyclerView("今日");
        if (isGrantedUsagePremission(getActivity())){
            ana_today.setVisibility(8);
            ana_week.setVisibility(8);
            TextView tv12 = getActivity().findViewById(R.id.textView12);
            tv12.setVisibility(8);
            recyclerView.setVisibility(8);
        }
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("WrongConstant")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (isGrantedUsagePremission(getActivity())){
                    ana_today.setVisibility(8);
                    ana_week.setVisibility(8);
                    TextView tv12 = getActivity().findViewById(R.id.textView12);
                    tv12.setVisibility(8);
                    recyclerView.setVisibility(8);
                    setRecyclerView(tab.getText().toString());
                    return;
                }else{
                    ana_today.setVisibility(0);
                    ana_week.setVisibility(0);
                    TextView tv12 = getActivity().findViewById(R.id.textView12);
                    tv12.setVisibility(0);
                    recyclerView.setVisibility(0);
                }
                if(tab.getText().toString().equals("今日")){
                    ana_today.setVisibility(View.VISIBLE);
                    ana_week.setVisibility(View.GONE);
                }else{
                    ana_today.setVisibility(View.GONE);
                    ana_week.setVisibility(View.VISIBLE);
                }
                setRecyclerView(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // refresh data
                setRecyclerView(tab.getText().toString());
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setRecyclerView(String type){
        // the list to memory data
        List<Drawable> drawables = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> pnames = new ArrayList<>();
        List<String> times = new ArrayList<>();
        int alltime = 0; // remember the time user has used
        List<Map.Entry<String, Long>> data = getapptime(type);
        if (data==null){
            return;
        }
        int k = 0;
        for(Map.Entry<String, Long> elem:data){
            // times and timesoflong
            int temp = elem.getValue().intValue();
            times.add(""+temp);
            // name and icon 此处未加null的情况！！！
            AppInfo appinfo = new AppInfo(getActivity(), elem.getKey());
            pnames.add(elem.getKey());
            names.add(appinfo.getAppname());
            drawables.add(appinfo.getAppicon());
            if(++k>=10)break;
        }
        for(Map.Entry<String, Long> elem:data){
            alltime += elem.getValue().intValue();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        recyclerView.setLayoutManager(layoutManager); // 设置布局管理器
        adapter_ana mAdapter = new adapter_ana(getActivity(), names, times, drawables, alltime, pnames);
        recyclerView.setAdapter(mAdapter);//设置Adapter
        if (type.equals("今日")){
            // set ana_today
            int hour = alltime/60;
            int minute = alltime%60;
            String time = "";
            if(hour!=0 )time += hour+"小时";
            if(minute!=0)time += minute+"分钟";
            if(time.equals(""))time="<1分钟";
            int parcent = (hour*60+minute)*100/1440;
            pb.setProgress(parcent);
            tv_usetime.setText(time);
            tv_percent.setText(parcent+"%的时间在屏幕上");

        }else{
            // set ana_week
            // 1. get data
            List<Integer> week_data = weekdata();
            List<BarEntry> values = new ArrayList<>();
            for(int i=0;i<week_data.size();i++){
                int elem = week_data.get(i);
                values.add(new BarEntry(i+1, elem));
            }
            BarDataSet dataSet = new BarDataSet(values,"");
            dataSet.setColor(Color.parseColor("#4267B1"));
            dataSet.setHighLightColor(Color.parseColor("#043B8F"));
            BarData barData = new BarData(dataSet);
            barData.setDrawValues(false);
            barData.setBarWidth(0.2f);
            // 2. beautify UI
            ana_week.getDescription().setEnabled(false); // 去除图表描述
            ana_week.getLegend().setEnabled(false);      // 去除图例
            XAxis xAxis = ana_week.getXAxis();           //  x轴
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(12f);
            xAxis.setTextColor(Color.parseColor("#555555"));
            xAxis.setLabelRotationAngle(-25);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setLabelCount(week_data.size());
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
//                    return super.getFormattedValue(value);
                }
            });
            ana_week.getAxisLeft().setEnabled(false);
            YAxis yAxis = ana_week.getAxisRight();        //  y轴
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
//                    return super.getFormattedValue(value);
                }
            });
            ana_week.setDragEnabled(true);
            ana_week.setPinchZoom(false);
            ana_week.setScaleEnabled(false);
            ana_week.setDrawGridBackground(true);
            ana_week.setGridBackgroundColor(Color.parseColor("#FAFAFA"));
            // 3. set data
            ana_week.setData(barData);
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setBgRadius(View layoutContent, final int bgRadius) {
        layoutContent.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), bgRadius);
            }
        });
        layoutContent.setClipToOutline(true);
    }
    // 获取七天的应用统计信息 返回一个长度为7的列表（可能小于7）
    public List<Integer> weekdata(){
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
        List<Integer> res = new ArrayList<>();
        UsageStatsManager usm = (UsageStatsManager)getActivity().getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Collections.reverse(calendars);
        for(int i=0;i<7;i++){
            Long end = calendars.get(i+1);
            Long start = calendars.get(i);
            int sum_minute = 0;
            List<UsageStats> USlist = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end);
            for(UsageStats elem:USlist){
                long elem_time = elem.getTotalTimeInForeground();
                Long temp = TimeUnit.MILLISECONDS.toMinutes(elem_time);
                sum_minute += temp.intValue();
            }
            res.add(sum_minute);
        }
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            int index = tablayout.getSelectedTabPosition();
            TabLayout.Tab tab = tablayout.getTabAt(index);
            setRecyclerView(tab.getText().toString());
        }

    }

    // 获取应用使用信息
    @RequiresApi(api = Build.VERSION_CODES.M)
    public List<Map.Entry<String, Long>> getapptime(String func){
        // 判断权限状况
        if (isGrantedUsagePremission(getActivity()) && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)  {
            try {
                // guide to setting to open Authority

                AlertDialog alertDialog2 = new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("请开启相关权限")
                        .setPositiveButton("前往开启", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            }
                        })

                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })

                        .create();
                alertDialog2.show();
            } catch (Exception e) {
                Toast.makeText(getActivity(),"无法开启允许查看使用情况的应用界面",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            // 获取 UsageStatesManager 对象
            UsageStatsManager usm = (UsageStatsManager)getActivity().getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            int y = c1.get(Calendar.YEAR);
            int m = c1.get(Calendar.MONTH);
            int d = c1.get(Calendar.DATE);
            c2.set(y, m, d, 0, 0, 0);
            List<UsageStats> USlist;
            if(func.equals("一周")){
                USlist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, c2.getTimeInMillis()-6*60*24*1000*60, c1.getTimeInMillis());
            }else{
                USlist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, c2.getTimeInMillis(), c1.getTimeInMillis());
            }
            // 获取 UsageStats 对象
            // 创建数组来存储数据
            Map<String, Long> res = new TreeMap<>();
            int index = 0;
            // 遍历并存储所有应用程序的包名和总使用时间 当使用时间不为0
            for (UsageStats us: USlist){
                String pname = us.getPackageName();
                long alltime = us.getTotalTimeInForeground();
                PackageManager pm = getActivity().getPackageManager();
                PackageInfo packageInfo;
                try {
                    packageInfo = pm.getPackageInfo(pname, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    packageInfo = null;
                    continue;
                }
                ApplicationInfo appinfo;
                try {
                    appinfo = pm.getApplicationInfo(pname, PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {
                    appinfo= null;
                    continue;
                }
                Long temp = TimeUnit.MILLISECONDS.toMinutes(alltime);
                if(temp>0 ){
                    // pname alltime
                    if(res.containsKey(pname)){
                        res.put(pname,res.get(pname)+ temp);
                    }else{
                        res.put(pname, temp);
                    }
                }
            }

            // 排序
            List<Map.Entry<String, Long>> re = new ArrayList<>(res.entrySet());
            Collections.sort(re, new Comparator<Map.Entry<String, Long>>() {
                @Override
                public int compare(Map.Entry<String, Long> t1, Map.Entry<String, Long> t2) {
                    if(t1.getValue() > t2.getValue()) return -1;
                    return 1;
                }
            });
            return re;
        }
        return null;
    }
    // 判断是否具有权限
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isGrantedUsagePremission(Context context) {
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        assert appOps != null;
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (context.checkCallingOrSelfPermission(
                    android.Manifest.permission.PACKAGE_USAGE_STATS)
                    == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        return !granted;
    }
}
