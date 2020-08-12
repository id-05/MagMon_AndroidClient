package com.example.magnetmonitorclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DateBase extends SQLiteOpenHelper {

    public DateBase(Context context) {
        super(context, "magmonclient", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase magmonDB) {
        String SQL =
                "create table servers ("
                        + "id integer primary key autoincrement,"
                        + "name text,"
                        + "ip text,"
                        + "port text" + ");";
        magmonDB.execSQL(SQL);

        SQL =
                "create table magmons ("
                        + "id integer primary key autoincrement,"
                        + "name text,"
                        + "HePress text,"
                        + "HeLevel text,"
                        + "WaterFlow1 text,"
                        + "WaterTemp1 text,"
                        + "WaterFlow2 text,"
                        + "WaterTemp2 text,"
                        + "Errors text,"
                        + "LastTime text,"
                        + "ServerID text" + ");";
        magmonDB.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase magmonDB, int i, int i1) {

    }
}