package com.example.endterm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(@Nullable Context context, int version) {
        super(context, "AppTimeDataBase", null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("TAG","创建了数据库。。creat()");
        String sql="create table apptime(_id integer primary key autoincrement,packagename varchar unique,curtime varchar,alltime varchar,msg varchar)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
