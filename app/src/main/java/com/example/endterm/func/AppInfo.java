package com.example.endterm.func;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppInfo {
    // 应用名 包名 应用图标 context
    String appname;
    String apppackname;
    Drawable appicon;
    Context context;
    public AppInfo(Context context, String apppackname){
        this.apppackname = apppackname;
        this.context = context;
        appname = getAppname();
    }
    public String getAppname(){
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(apppackname, PackageManager.GET_META_DATA);
            return pm.getApplicationLabel(appInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Drawable getAppicon(){
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(apppackname, PackageManager.GET_META_DATA);
            return pm.getApplicationIcon(apppackname);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
