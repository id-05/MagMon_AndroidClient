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
                        +"connect integer,"
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
                        + "MonitoringEnabled integer,"
                        + "HeLevelCurrent text,"
                        + "HeLevelTopCurrent text,"
                        + "HeLevelTop text,"
                        + "ReconRuOCurrent text,"
                        + "ReconRuO text,"
                        + "SpareScanRoom1A text,"
                        + "ShieldTemp text,"
                        + "ReconSi410Current text,"
                        + "ReconSi410 text,"
                        + "SpareScanRoom1B text,"
                        + "ColdheadRuOCurrent text,"
                        + "ColdheadTemp text,"
                        + "SCPressure text,"
                        + "SpareCmp1b text,"
                        + "SpareCmp1c text,"
                        + "ReconSi4102a text,"
                        + "ReconSi4102aCurrent text,"
                        + "ReconSi4102b text,"
                        + "ReconSi4102bCurrent text,"
                        + "VoltsPlusExternal text,"
                        + "VoltsPlus text,"
                        + "VoltsMinus text,"
                        + "VoltsMinusExternal text,"
                        + "HFOBottomShield text,"
                        + "MagmonCaseTemp text,"
        + "ServerID integer" + ");";
        magmonDB.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase magmonDB, int i, int i1) {

    }
}