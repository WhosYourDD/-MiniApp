package com.example.endterm.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.endterm.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link user#newInstance} factory method to
 * create an instance of this fragment.
 */
public class user extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout help;
    private LinearLayout help_tab;
    private LinearLayout permission_tab;
    private LinearLayout permission;
    private LinearLayout about;
    private LinearLayout about_tab;
    private Button btn1;
    private Button btn2;
    public user() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment user.
     */
    // TODO: Rename and change types and number of parameters
    public static user newInstance(String param1, String param2) {
        user fragment = new user();
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        help = getActivity().findViewById(R.id.help);
        help_tab = getActivity().findViewById(R.id.help_tab);
        permission = getActivity().findViewById(R.id.permission);
        permission_tab = getActivity().findViewById(R.id.permission_tab);
        about = getActivity().findViewById(R.id.about);
        about_tab = getActivity().findViewById(R.id.about_tab);
        btn1 = getActivity().findViewById(R.id.btn_p1);
        btn2 = getActivity().findViewById(R.id.btn_p2);
        help_tab.setVisibility(8);
        permission_tab.setVisibility(8);
        about_tab.setVisibility(8);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (help_tab.getVisibility()==8)help_tab.setVisibility(0);
                else help_tab.setVisibility(8);
                permission_tab.setVisibility(8);
                about_tab.setVisibility(8);
            }
        });
        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help_tab.setVisibility(8);
                if (permission_tab.getVisibility()==8)permission_tab.setVisibility(0);
                else permission_tab.setVisibility(8);
                about_tab.setVisibility(8);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help_tab.setVisibility(8);
                permission_tab.setVisibility(8);
                if (about_tab.getVisibility()==8)about_tab.setVisibility(0);
                else about_tab.setVisibility(8);
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent();
                //直接跳转到应用通知设置的代码：设置提示 优化弹窗 让用户点击一个button去跳转 然后给权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上到8.0以下
                    localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    localIntent.putExtra("app_package", getActivity().getPackageName());
                    localIntent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
                } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
                    localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    localIntent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                } else {
                    //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= 9) {
                        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        localIntent.setAction(Intent.ACTION_VIEW);
                        localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                        localIntent.putExtra("com.android.settings.ApplicationPkgName", getActivity().getPackageName());
                    }
                }
                startActivity(localIntent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });
    }
}
