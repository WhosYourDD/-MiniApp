package com.example.endterm.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.endterm.MainActivity;
import com.example.endterm.R;
import com.example.endterm.database.DataBaseHelper;
import com.example.endterm.func.AppInfo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class Notification extends Service {
    public Notification() {
    }
    final Timer timer = new Timer();
    NotificationManager manager;
    NotificationCompat.Builder builder;
    String lastapp = "null"; // 记录上次弹框的app
    int channelId = 101; // ++
    public static final int NOTICE_ID = 100;
    Calendar calendar = Calendar.getInstance(); // 记录上次弹框的时间
    // 这里最好维护一个列表存储n分钟内曾经弹框提醒过的 将不再提醒

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("NotifiyService", "Current Create ");

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(!isGrantedUsagePremission(getApplicationContext())){
                        return;
                    }
                }
                String curapp_packagename = getTaskPackname();

                String timetocompare = "-1";
                DataBaseHelper dbhelper=new DataBaseHelper(getApplicationContext(),1);
                SQLiteDatabase datebase = dbhelper.getReadableDatabase();
                Cursor cursor=datebase.query("apptime",null,null,null,null,null,null);
                int count=cursor.getCount();
                String message="";
                while(cursor.moveToNext()){
                    int id=cursor.getInt(0);
                    String packagename=cursor.getString(cursor.getColumnIndex("packagename"));
                    String ctime=cursor.getString(cursor.getColumnIndex("curtime"));
                    String atime=cursor.getString(cursor.getColumnIndex("alltime"));
                    message=cursor.getString(cursor.getColumnIndex("msg"));
                    if (packagename.equals(curapp_packagename)){
                        timetocompare = atime;
                        break;
                    }
                }
                int curapp_usedtime = getUsedTime(curapp_packagename);
                ContentValues values=new ContentValues();
                values.put("packagename",curapp_packagename);
                values.put("curtime",curapp_usedtime+"");
                values.put("alltime",timetocompare);
                values.put("msg",message);
                // int IndexOfPname=apppackagenames.indexOf(pname);

                if (!timetocompare.equals("-1"))
                {int updatecount=datebase.update("apptime",values,"packagename=?",new String[]{curapp_packagename});}

                cursor.close();
                datebase.close();
                if (timetocompare.equals("-1")){
                    return;
                }
                int cur = Integer.parseInt(timetocompare);
                String time="";
                if (cur/60>0){
                    time+=cur/60+"h";
                }
                if (cur%60>0){
                    time += cur%60+"m";
                }

                Calendar temp = Calendar.getInstance();
                AppInfo appinfo = new AppInfo(getApplicationContext(), curapp_packagename);
                if (curapp_usedtime>=cur && (!curapp_packagename.equals(lastapp) || temp.getTimeInMillis()-calendar.getTimeInMillis()>=1000*60*15)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){    //Android 8.0以上适配
                        NotificationChannel channel = new NotificationChannel(String.valueOf(channelId),appinfo.getAppname(),
                                NotificationManager.IMPORTANCE_HIGH);
                        manager.createNotificationChannel(channel);
                        builder = new NotificationCompat.Builder(getApplicationContext(),String.valueOf(channelId));
                    }else{
                        builder = new NotificationCompat.Builder(getApplicationContext());
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),1,intent,0);
                    builder.setContentTitle(appinfo.getAppname())            //指定通知栏的标题内容
                            .setContentText("今日已使用该应用超过设定值"+time+"，请合理使用")             //通知的正文内容
                            .setWhen(System.currentTimeMillis())                //通知创建的时间 通常不需要
                            .setSmallIcon(R.drawable.icon3)    //通知显示的小图标，只能用alpha图层的图片进行设置
                            .setDefaults(android.app.Notification.DEFAULT_VIBRATE) // 震动 闪光  声音
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_background));

                    android.app.Notification notification = builder.build() ;
                    manager.notify(channelId,notification);
                    channelId+=1;
                    lastapp = curapp_packagename;
                    calendar=temp;
                }

            }
        }, 1000, 1000L);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("NotifiyService", "Current Start ");
//        return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Log.i("NotifiyService", "Current onDestroy ");
        // 如果Service被杀死，干掉通知
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
//            NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//            mManager.cancel(NOTICE_ID);          }
//        // 重启自己
//        Intent intent = new Intent(getApplicationContext(),NotifiyService.class);
//        startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("NewApi")
    private String getTaskPackname() {
        String currentApp = "CurrentNULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        Log.i("NotifiyService", "Current App in foreground is: " + currentApp);
        return currentApp;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private int getUsedTime(String packagename){
        UsageStatsManager usm = (UsageStatsManager)this.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        int y = c1.get(Calendar.YEAR);
        int m = c1.get(Calendar.MONTH);
        int d = c1.get(Calendar.DATE);
        c2.set(y, m, d, 0, 0, 0);
        List<UsageStats> USlist;
        USlist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, c2.getTimeInMillis(), c1.getTimeInMillis());
        for(UsageStats elem:USlist){
            if (elem.getPackageName().equals(packagename)){
                Date date = new Date();
                date.setTime(elem.getTotalTimeInForeground());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR,-8);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                return hour*60*60+minute*60;
            }
        }
        return 0;
    }
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
        return granted;
    }
}
