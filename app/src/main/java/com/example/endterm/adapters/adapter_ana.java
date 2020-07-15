package com.example.endterm.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.endterm.DetailActivity;
import com.example.endterm.MainActivity;
import com.example.endterm.R;
import com.example.endterm.StartActivity;

import java.util.List;

public class adapter_ana extends RecyclerView.Adapter<adapter_ana.ViewHolder>{

    private List<String> data1s = null;     // 应用名
    private List<String> data2s = null;     // 使用时间
    private List<Drawable> data3s = null;   // 应用图标
    private List<String> pnames = null;   // packagename
    private int alltime = 0;            // 以分钟为单位的总使用时间
    private Context mContext = null;        // context
    public final static String EXTRA_MESSAGE = "MESSAGE";

    public adapter_ana(Context c, List<String> data1s, List<String> data2s, List<Drawable> data3s,int alltime, List<String> pnames){
        this.data1s = data1s;
        this.data2s = data2s;
        this.data3s = data3s;
        this.alltime = alltime;
        this.pnames = pnames;
        this.mContext = c;
    }
    @NonNull
    @Override
    public adapter_ana.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_ana,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull adapter_ana.ViewHolder holder, final int position) {
        holder.mTextView1.setText(position+1+". "+data1s.get(position));
        int usetime = Integer.parseInt( data2s.get(position));
        holder.mTextView2.setText(helper(usetime));
        holder.mImageView1.setImageDrawable(data3s.get(position));
        holder.mprogtessbar.setMax(100);
        holder.mprogtessbar.setProgress(Long.valueOf(usetime*100/alltime).intValue());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 单击某个应用时的响应事件
                Toast.makeText(mContext, data1s.get(position), Toast.LENGTH_SHORT).show();
                // 跳转至该应用的详情页
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE,pnames.get(position));
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return data1s.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView1; // name
        TextView mTextView2; // time
        ImageView mImageView1;
        ProgressBar mprogtessbar;

        public ViewHolder(@NonNull View view) {
            super(view);
            mTextView1 = (TextView) view.findViewById(R.id.timeshow_tv2);
            mTextView2 = (TextView) view.findViewById(R.id.timeshow_tv1);
            mImageView1 = (ImageView) view.findViewById(R.id.timeshow_img1);
            mprogtessbar = view.findViewById(R.id.ana_today_pb);
        }
    }

    private String helper(int x){
        String re = "";
        long hour = x/60;
        long minute = x%60;
        if(hour>0){
            if (minute>0) re = hour+"时"+minute+"分";
            else re=hour+"时";
        }else if (minute>0){
            re = minute+"分";
        }else{
            re = "<1分钟>";
        }
        return re;
    }

}
