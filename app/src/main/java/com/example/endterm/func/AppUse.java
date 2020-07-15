package com.example.endterm.func;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppUse {
    private String packagename;
    private Context mt;
    private String usetime;
    private Long usetimeoflong;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public AppUse(Context context, String packagename){
        this.packagename = packagename;
        this.mt = context;
        UsageStatsManager usm = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        int y = c1.get(Calendar.YEAR);
        int m = c1.get(Calendar.MONTH);
        int d = c1.get(Calendar.DATE);
        c2.set(y, m, d, 0, 0, 0);
        List<UsageStats> USlist;
        assert usm != null;
        USlist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, c2.getTimeInMillis(), c1.getTimeInMillis());
        for (UsageStats us: USlist){
            if(us.getPackageName().equals(packagename)){
                long alltime = us.getTotalTimeInForeground();
                Date date = new Date();
                date.setTime(alltime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR,-8);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                this.usetime = hour*60+minute+"";
                this.usetimeoflong = alltime;
                break;
            }
        }
    }

    public String gettime(){
        if (this.usetime==null){
            return "未使用";
        }
        return this.usetime;
    }
    public Long gettimelong(){
        if (this.usetime==null){
            return 0L;
        }
        return this.usetimeoflong;
    }
}
