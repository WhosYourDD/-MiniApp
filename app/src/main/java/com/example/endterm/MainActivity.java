package com.example.endterm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.example.endterm.navigation.ana;
import com.example.endterm.navigation.plan;
import com.example.endterm.navigation.user;
import com.example.endterm.service.JobHandlerService;
import com.example.endterm.service.LocalService;
import com.example.endterm.service.Notification;
import com.example.endterm.service.RemoteService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.endterm.MESSAGE";
    private FrameLayout frameLayout;
    private RadioButton btn_1;
    private RadioButton btn_2;
    private RadioButton btn_3;
    private Toolbar toolbar;

    private FragmentManager fragmentManager;
    private Fragment currentFragment = new Fragment();
    private List<Fragment> fragments = new ArrayList<>();

    private int index_cur = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindview();
        bindclicklistener();
    }

    public void bindview(){
        frameLayout = findViewById(R.id.framelayout);
        btn_1 = findViewById(R.id.btn_nav_1);
        btn_2 = findViewById(R.id.btn_nav_2);
        btn_3 = findViewById(R.id.btn_nav_3);
        toolbar = findViewById(R.id.toolbar);
        // init the elements
        btn_1.setTextColor(Color.rgb(26,188,156));
        fragmentManager = getSupportFragmentManager();
        fragments.add(new plan());
        fragments.add(new ana());
        fragments.add(new user());
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.framelayout, fragments.get(0)).add(R.id.framelayout, fragments.get(1)).add(R.id.framelayout, fragments.get(2));
        ft.hide(fragments.get(1)).hide(fragments.get(2));
        ft.commit();
        toolbar.setTitle("日程安排");
    }

    public void bindclicklistener(){
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 修改 button 样式， 设置选中态：字体颜色+图片
                btn_1.setTextColor(Color.rgb(26,188,156));
                btn_2.setTextColor(Color.BLACK);
                btn_3.setTextColor(Color.BLACK);
                btn_1.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.calendar_25_selected),null,null);
                btn_2.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.bookmark_25),null,null);
                btn_3.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.settings_25),null,null);
                // 修改页面
                change_nav(0);
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_2.setTextColor(Color.rgb(26,188,156));
                btn_1.setTextColor(Color.BLACK);
                btn_3.setTextColor(Color.BLACK);
                btn_1.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.calendar_25),null,null);
                btn_2.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.bookmark_25_selected),null,null);
                btn_3.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.settings_25),null,null);
                change_nav(1);
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_3.setTextColor(Color.rgb(26,188,156));
                btn_1.setTextColor(Color.BLACK);
                btn_2.setTextColor(Color.BLACK);
                btn_1.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.calendar_25),null,null);
                btn_2.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.bookmark_25),null,null);
                btn_3.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.settings_25_selected),null,null);
                change_nav(2);
            }
        });
    }

    public void change_nav(int index){
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (index == index_cur)return;
        ft.hide(fragments.get(index_cur)).show(fragments.get(index));
        ft.commit();
        index_cur = index;
        if(index_cur == 0)toolbar.setTitle("日程安排");
        else if(index_cur == 1)toolbar.setTitle("应用统计");
        else if(index_cur == 2)toolbar.setTitle("个人中心");

    }
}
