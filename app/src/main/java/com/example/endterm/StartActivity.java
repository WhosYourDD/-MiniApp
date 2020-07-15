package com.example.endterm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.endterm.service.LocalService;
import com.example.endterm.service.Notification;
import com.example.endterm.service.RemoteService;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_start);
        //
        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, RemoteService.class));
        startService(new Intent(this, Notification.class));

        Thread myThread=new Thread(){//创建子线程
            @Override
            public void run() {
                try{
                    sleep(3000);//使程序休眠五秒
                    Intent it=new Intent(getApplicationContext(),MainActivity.class);//启动MainActivity
                    startActivity(it);
                    finish();//关闭当前活动
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程


    }
}
