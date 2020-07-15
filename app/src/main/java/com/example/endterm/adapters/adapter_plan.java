package com.example.endterm.adapters;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.endterm.R;
import com.example.endterm.database.DataBaseHelper;
import com.example.endterm.func.AppInfo;

import java.util.List;

public class adapter_plan extends RecyclerView.Adapter<adapter_plan.ViewHolder>{
    private List<String> apppackagenames;
    private List<String> curtime;
    private List<String> alltime;
    private List<String> msg;
    private Context mt;

    public adapter_plan(Context context, List<String> apppackagenames, List<String> curtime, List<String> alltime, List<String> msg){
        this.apppackagenames=apppackagenames;
        this.curtime=curtime;
        this.alltime=alltime;
        this.msg = msg;
        mt = context;
    }

    @NonNull
    @Override
    public adapter_plan.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home,parent,false);
        final adapter_plan.ViewHolder holder = new adapter_plan.ViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull adapter_plan.ViewHolder holder, final int position) {
        final AppInfo appinfo = new AppInfo(mt, apppackagenames.get(position));
        holder.mtextview1.setText(appinfo.getAppname());
//        holder.mtextview2.setText(curtime.get(position)+"/"+alltime.get(position));
        holder.mtextview3.setText(msg.get(position));
        holder.mimageview.setImageDrawable(appinfo.getAppicon());
        int all = Integer.parseInt(alltime.get(position));
        if (curtime.get(position).equals("未使用")){
            holder.progressBar.setProgress(0);
            holder.mtextview2.setText("未使用/"+helper(all));
        }
        else{
            int cur = Integer.parseInt(curtime.get(position));

            if (cur<all){
                holder.mtextview2.setText(helper(cur)+"/"+helper(all));
                holder.progressBar.setProgress(cur*100/all);
            }else{
                holder.mtextview2.setText("已超时"+helper(cur-all));
                holder.progressBar.setProgress(100);
            }
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mt);
                builder.setMessage("确定删除这一项吗")
                        .setTitle("提示");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        // 在数据库中删除这一项
                        delitem(position);
                        Log.i("PlanActivity", "You have delete "+appinfo.getAppname());
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                builder.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return apppackagenames.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView mtextview1;
        TextView mtextview2;
        TextView mtextview3;
        ImageView mimageview;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mtextview1 = itemView.findViewById(R.id.textView1);
            mtextview2 = itemView.findViewById(R.id.textView2);
            mtextview3 = itemView.findViewById(R.id.textView3);
            mimageview = itemView.findViewById(R.id.imageView);
            progressBar = itemView.findViewById(R.id.progressBar3);
        }
    }
    public void delitem(int position){
        // 在数据库中删除
        Log.e("TAG","删除的position:"+position+"-->"+apppackagenames.get(position));
        DataBaseHelper dbhelper=new DataBaseHelper(mt,1);
        SQLiteDatabase datebase = dbhelper.getReadableDatabase();
        int deletecount=datebase.delete("apptime","packagename=?",new String[]{apppackagenames.get(position)});
        datebase.close();
        Log.e("TAG","数据删除完成:"+deletecount);
        // 删除完毕
        apppackagenames.remove(position);
        curtime.remove(position);
        alltime.remove(position);
        msg.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, curtime.size()-position);
    }

    public void additem(String pname, String ctime, String atime, String message){
        DataBaseHelper dbhelper=new DataBaseHelper(mt,1);
        SQLiteDatabase datebase = dbhelper.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("packagename",pname);
        values.put("curtime",ctime);
        values.put("alltime",atime);
        values.put("msg",message);
        //这里也可以选择在数据库查，cursor==1代表table有该包名：更新；==0代表没有：插入
        //Cursor cursor=datebase.query("apptime",null,"packagename=?",new String[]{pname},null,null,null);
        //Log.e("TAG", "cursor:(==1代表table有该包名)"+String.valueOf(cursor.getCount()));
        int IndexOfPname=apppackagenames.indexOf(pname);
        Log.e("TAG","jj："+IndexOfPname);
        if(IndexOfPname==-1){
            //未找到该索引，插入操作
            apppackagenames.add(pname);
             curtime.add(ctime);
            alltime.add(atime);
            msg.add(message);
            long id=datebase.insert("apptime",null,values);
            Log.e("TAG","插入完成");
        }else {
            //有该panme的索引，更新操作
            int updatecount=datebase.update("apptime",values,"packagename=?",new String[]{pname});
            Log.e("TAG","数据更新完成");
            int ii=apppackagenames.indexOf(pname);
            alltime.set(ii,atime);
            curtime.set(ii,ctime);
            msg.set(ii,message);
        }
        //关闭连接
        datebase.close();
        // 添加完毕
        notifyItemInserted(msg.size()-1);
    }

    public void update(){

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
            re = "未使用";
        }
        return re;
    }
}
