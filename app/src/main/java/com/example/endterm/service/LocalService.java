package com.example.endterm.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.example.endterm.GuardAidl;

public class LocalService extends Service {
    private ServiceConnection conn;
    private MyService myService;
    public LocalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();

    }

    private void init() {
        if (conn == null) {
            conn = new MyServiceConnection();
        }
        myService = new MyService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(getApplicationContext(), "本地进程启动", Toast.LENGTH_LONG).show();
        Log.d("Local:","本地进程启动");

        Intent intents = new Intent();
        intents.setClass(this, RemoteService.class);
        bindService(intents, conn, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    class MyService extends GuardAidl.Stub {


        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }

        @Override
        public String getName() throws RemoteException {
            return null;
        }
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("获取连接");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(LocalService.this, "远程连接被干掉了", Toast.LENGTH_SHORT).show();
            Log.d("Local:","远程连接断开");
            LocalService.this.startService(new Intent(LocalService.this,
                    RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this,
                    RemoteService.class), conn, Context.BIND_IMPORTANT);

        }

    }
}
