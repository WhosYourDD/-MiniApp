package com.example.endterm.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.endterm.R;


public class add_plan extends DialogFragment {
    // 监听接口
    public interface DataBackListener{
        public void getData(String datatoback);
    }
    private EditText et_title;
    private EditText et_date_h;
    private EditText et_date_m;
    private EditText et_msg;
    private Button btn_yes;
    private Button btn_back;
    DataBackListener listener;   //创建监听对象
    String data;

    public add_plan(DataBackListener listener){
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_plan, null);
        et_title = view.findViewById(R.id.et_title);
        et_date_h = view.findViewById(R.id.et_date_h);
        et_date_m = view.findViewById(R.id.et_date_m);
        et_msg = view.findViewById(R.id.et_msg);
        btn_yes = view.findViewById(R.id.button6);
        btn_back = view.findViewById(R.id.button7);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str1= et_title.getText().toString();
                String str2= et_date_h.getText().toString();
                String str3= et_date_m.getText().toString();
                String str4= et_msg.getText().toString();
                int alltime = 0;
                int h= str2.isEmpty()?0:Integer.parseInt(str2);
                int m= str3.isEmpty()?0:Integer.parseInt(str3);
                if(str1.isEmpty()) Toast.makeText(getActivity(), "请填写app名字", Toast.LENGTH_SHORT).show();
                else if(h+m==0||h>=24||m>=60||h*60+m>=24*60||h<0||m<0) Toast.makeText(getActivity(), "请正确填写最长使用时间", Toast.LENGTH_SHORT).show();
                else if(str4.isEmpty()) Toast.makeText(getActivity(), "请填写备注", Toast.LENGTH_SHORT).show();
                else{
                    alltime = h*60+m;
                    listener.getData(str1+"|"+alltime+"|"+str4);
                    dismiss();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

}
