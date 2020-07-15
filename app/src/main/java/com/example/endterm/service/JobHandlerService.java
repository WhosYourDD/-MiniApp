package com.example.endterm.service;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.List;

public class JobHandlerService extends JobService {
    private JobScheduler mJobScheduler;

    public JobHandlerService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("服务被创建");

//        startService(new Intent(this, LocalService.class));
//        startService(new Intent(this, RemoteService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(startId++,
                    new ComponentName(getPackageName(), JobHandlerService.class.getName()));

            builder.setPeriodic(5000); //每隔5秒运行一次
            builder.setRequiresCharging(true);
            builder.setPersisted(true);  //设置设备重启后，是否重新执行任务
            builder.setRequiresDeviceIdle(true);

            if (mJobScheduler.schedule(builder.build()) <= 0) {
                //If something goes wrong
                System.out.println("工作失败");
            } else {
                System.out.println("工作成功");
            }
        }
        return START_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        System.out.println("msgrun");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("TAG", "msgonstopjob");
        if (isServiceRunning(this, "com.example.endterm.LocalService") || isServiceRunning(this, "com.example.endterm.RemoteService")) {
            startService(new Intent(this, LocalService.class));
            startService(new Intent(this, RemoteService.class));
        }
        return false;
    }

    // 服务是否运行
    public boolean isServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();


        for (ActivityManager.RunningAppProcessInfo info : lists) {// 获取运行服务再启动
            System.out.println(info.processName);
            if (info.processName.equals(serviceName)) {
                isRunning = true;
            }
        }
        return !isRunning;

    }

}
