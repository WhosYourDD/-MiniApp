package com.example.endterm.navigation;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import com.example.endterm.MainActivity;
import com.example.endterm.R;
import com.example.endterm.adapters.adapter_plan;
import com.example.endterm.database.DataBaseHelper;
import com.example.endterm.fragments.add_plan;
import com.example.endterm.func.AppUse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class plan extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    // my param
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private List<Apps> appList;
    private adapter_plan mAdapter;

    public plan() {
        // Required empty public constructor
    }


    public static plan newInstance(String param1, String param2) {
        plan fragment = new plan();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // find app installed
        try{
            appList=new ArrayList<>();
            List<PackageInfo> packages=getActivity().getPackageManager().getInstalledPackages(0);
            for(int i=0;i<packages.size();++i){
                PackageInfo packageInfo=packages.get(i);
                Apps tmpInfo=new Apps();
                tmpInfo.appName=packageInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
                tmpInfo.packageName=packageInfo.packageName;
                tmpInfo.versionName=packageInfo.versionName;
                tmpInfo.versionCode=packageInfo.versionCode;
                tmpInfo.appIcon=packageInfo.applicationInfo.loadIcon(getActivity().getPackageManager());

                //获取非系统应用
                if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0){
                    appList.add(tmpInfo);
                }
                //appList.add(tmpInfo);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        // bind
        fab = getActivity().findViewById(R.id.fab);
        recyclerView = getActivity().findViewById(R.id.recycleview_plan);
        setBgRadius(recyclerView, 20);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager); // 设置布局管理器
        // get data from sqlite
        List<String> pname = new ArrayList<>();
        List<String> curtime = new ArrayList<>();
        List<String> alltime = new ArrayList<>();
        List<String> msg = new ArrayList<>();
        DataBaseHelper dbhelper=new DataBaseHelper(getActivity(),1);
        SQLiteDatabase datebase = dbhelper.getReadableDatabase();
        Cursor cursor=datebase.query("apptime",null,null,null,null,null,null);
        //总的匹配数量
        int count=cursor.getCount();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String packagename=cursor.getString(cursor.getColumnIndex("packagename"));
            String ctime=cursor.getString(cursor.getColumnIndex("curtime"));
            String atime=cursor.getString(cursor.getColumnIndex("alltime"));
            String message=cursor.getString(cursor.getColumnIndex("msg"));
            pname.add(packagename);
            curtime.add(ctime);
            alltime.add(atime);
            msg.add(message);
        }
        cursor.close();
        datebase.close();
        // set adapter
        mAdapter = new adapter_plan(getActivity(), pname, curtime, alltime, msg);
        recyclerView.setAdapter(mAdapter);//设置Adapter
        // click listener for fab
        fab.setOnTouchListener(new View.OnTouchListener() {
            int lastX,lastY;
            long startTime,endTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ea = event.getAction();
                DisplayMetrics metric = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
                final int screenWidth = metric.widthPixels;
                ViewGroup mViewGroup = (ViewGroup) fab.getParent();
                final int screenHeight = mViewGroup.getHeight();
                switch(ea){
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();// 获取触摸事件触摸位置的原始X坐标
                        lastY = (int) event.getRawY();
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        int l = v.getLeft() + dx;
                        int b = v.getBottom() + dy;
                        int r = v.getRight() + dx;
                        int t = v.getTop() + dy;
                        // 下面判断移动是否超出屏幕
                        if (l < 0) {
                            l = 0;
                            r = l + v.getWidth();
                        }
                        if (t < 0) {
                            t = 0;
                            b = t + v.getHeight();
                        }
                        if (r > screenWidth) {
                            r = screenWidth;
                            l = r - v.getWidth();
                        }
                        if (b > screenHeight) {
                            b = screenHeight;
                            t = b - v.getHeight();
                        }
                        v.layout(l, t, r, b);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        v.postInvalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        Log.i("PlanActivity", "onTouch: "+(endTime-startTime));
                        if ((endTime - startTime) < 0.15 * 1000L) {
                            // 长按事件 跳转至信息填写页并对其返回信息进行验证和处理
                            // jump to add_plan
                            DialogFragment newFragment = new add_plan(new add_plan.DataBackListener() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                                @Override
                                public void getData(String datatoback) {
                                    String[] result = datatoback.split("\\|");
                                    String appname = result[0];
                                    String maxtime = result[1];
                                    String msg = result[2];
                                    String pname;
                                    for(Apps elem:appList){
                                        if(elem.appName.equals(appname)){
                                            pname = elem.packageName;
                                            AppUse curapp = new AppUse(getActivity(), pname);
                                            mAdapter.additem(pname,curapp.gettime(),maxtime,msg);
                                            recyclerView.setAdapter(mAdapter);
                                            return;
                                        }
                                    }
                                    Toast.makeText(getActivity(), "未安装此应用",Toast.LENGTH_LONG).show();
                                }
                            });
                            newFragment.show(getActivity().getSupportFragmentManager(), "addplan");
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
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
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            // 当show时刷新数据
            List<String> pname = new ArrayList<>();
            List<String> curtime = new ArrayList<>();
            List<String> alltime = new ArrayList<>();
            List<String> msg = new ArrayList<>();
            // 从数据库读
            DataBaseHelper dbhelper=new DataBaseHelper(getActivity(),1);
            SQLiteDatabase datebase = dbhelper.getReadableDatabase();
            Cursor cursor=datebase.query("apptime",null,null,null,null,null,null);
            //总的匹配数量
            int count=cursor.getCount();
            while (cursor.moveToNext()){
                int id=cursor.getInt(0);
                String packagename=cursor.getString(cursor.getColumnIndex("packagename"));
                String ctime=cursor.getString(cursor.getColumnIndex("curtime"));
                String atime=cursor.getString(cursor.getColumnIndex("alltime"));
                String message=cursor.getString(cursor.getColumnIndex("msg"));
                pname.add(packagename);
                curtime.add(ctime);
                alltime.add(atime);
                msg.add(message);
                Log.e("TAG",packagename+"  "+ctime+"  "+atime+"  "+message);
            }
            cursor.close();
            datebase.close();
            // 读完
            mAdapter = new adapter_plan(getActivity(), pname, curtime, alltime, msg);
            recyclerView.setAdapter(mAdapter);//设置Adapter
        }
    }
}
class Apps{
    public String appName="";
    public String packageName="";
    public String versionName="";
    public int versionCode=0;
    public Drawable appIcon=null;
    public void print(){
        Log.v("app","Name:"+appName+" Package:"+packageName);
        Log.v("app","Name:"+appName+" versionName:"+versionName);
        Log.v("app","Name:"+appName+" versionCode:"+versionCode);
    }
}
