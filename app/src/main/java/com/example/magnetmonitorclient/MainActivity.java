package com.example.magnetmonitorclient;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DateBase dbHelper;
    public static MagMonCardAdapter adapter;
    public ArrayList<MagMonRec> MagMonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MagMon Client");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MagMonList=getMagMonList(this);
        startService(new Intent(MainActivity.this,MagMonService.class));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MagMonCardAdapter(MagMonList,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.TitleAddServer:
                Intent i = new Intent(MainActivity.this, AddMagMonServer.class);
                i.putExtra("method","new");
                startActivity(i);
                return true;
            case R.id.StopService:
                stopService(new Intent(MainActivity.this,MagMonService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void serverAddBase(MagMonServer server){
        ContentValues newValues = new ContentValues();
        newValues.put("name",server.Name);
        newValues.put("ip",server.IP);
        newValues.put("port",server.Port);
        try {
            SQLiteDatabase userDB = dbHelper.getWritableDatabase();
            userDB.insertOrThrow("servers", null, newValues);
            userDB.close();
        }catch (SQLException e){
            print("error add to base "+e.toString());
        }
    }

    public static void serverDelBase(MagMonServer server) {
        int id = server.getId();
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.delete("servers","id = " + id, null);
    }

    public static int magmonUpdateBase(MagMonRec magmon) {
        ContentValues newValues = new ContentValues();
        newValues.put("HePress",magmon.getHePress());
        newValues.put("HeLevel",magmon.getHeLevel());
        newValues.put("WaterFlow1",magmon.getWaterFlow1());
        newValues.put("WaterTemp1",magmon.getWaterTemp1());
        newValues.put("WaterFlow2",magmon.getWaterFlow2());
        newValues.put("WaterTemp2",magmon.getWaterTemp2());
        ///////////////////в этом месте была ошибка
        //print(Integer.toString(magmon.getErrors().size()));
        newValues.put("Errors",1);
        newValues.put("LastTime",magmon.getLastTime());
        if(magmon.getMonitoringEnabled()){
            newValues.put("MonitoringEnabled",1);
        }else{
            newValues.put("MonitoringEnabled",0);
        }
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        int buf = userDB.update("magmons", newValues, "name = ?",
                new String[] {magmon.Name});
        userDB.close();
        return buf;
    }

    public static void magmonDelBase(MagMonRec magmon) {
        int id = magmon.getId();
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.delete("magmons","id = " + id, null);
    }

    public static void serverUpdateBase(MagMonServer server) {
        int id = server.getId();
        ContentValues newValues = new ContentValues();
        newValues.put("name",server.getName());
        if(server.getConnect()){
            newValues.put("connect",1);
        }else{
            newValues.put("connect",0);
        }
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.update("servers", newValues, "id = ?",
                new String[] {String.valueOf(id)});
        userDB.close();
    }

    public static void print(String str){
        Log.d("MagMon",str);
    }

    public static ArrayList<MagMonServer> getServerList(Context context){
        dbHelper = new DateBase(context);
        ArrayList<MagMonServer> serverList = new ArrayList<>();
        serverList.clear();
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = userDB.query("servers", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    MagMonServer server = new MagMonServer();
                    server.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    server.setName(cursor.getString(cursor.getColumnIndex("name")));
                    server.setIP(cursor.getString(cursor.getColumnIndex("ip")));
                    server.setPort(cursor.getString(cursor.getColumnIndex("port")));
                    if(cursor.getInt(cursor.getColumnIndex("connect"))==1)
                    {
                        server.setConnect(true);
                    }else{
                        server.setConnect(false);
                    }
                    serverList.add(server);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            print("error get serverlist "+e.toString());
        }
        return serverList;
    }

    public static ArrayList<MagMonRec> getMagMonList(Context context){
        ArrayList<MagMonRec> MagMonList = new ArrayList<>();
        dbHelper = new DateBase(context);
        MagMonList.clear();
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = userDB.query("magmons", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    MagMonRec magmon = new MagMonRec();
                    magmon.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    magmon.setName(cursor.getString(cursor.getColumnIndex("name")));
                    magmon.setHePress(cursor.getString(cursor.getColumnIndex("HePress")));
                    magmon.setHeLevel(cursor.getString(cursor.getColumnIndex("HeLevel")));
                    magmon.setWaterFlow1(cursor.getString(cursor.getColumnIndex("WaterFlow1")));
                    magmon.setWaterTemp1(cursor.getString(cursor.getColumnIndex("WaterTemp1")));
                    magmon.setLastTime(cursor.getString(cursor.getColumnIndex("LastTime")));
                    if(cursor.getInt(cursor.getColumnIndex("MonitoringEnabled"))==1){
                        magmon.setMonitoringEnabled(true);
                    }else{
                        magmon.setMonitoringEnabled(false);
                    }
                    MagMonList.add(magmon);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            print(e.toString());
        }
        return MagMonList;
    }

    public static MagMonRec getMagMonByName(Context context, String widgetMagMon){
        dbHelper = new DateBase(context);
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        MagMonRec magmon = new MagMonRec();
        try {
            Cursor cursor = userDB.query("magmons", null, "name = ?", new String[]{widgetMagMon},
                    null, null, null);
            cursor.moveToFirst();
            magmon.setId(cursor.getInt(cursor.getColumnIndex("id")));
            magmon.setName(cursor.getString(cursor.getColumnIndex("name")));
            magmon.setHePress(cursor.getString(cursor.getColumnIndex("HePress")));
            magmon.setHeLevel(cursor.getString(cursor.getColumnIndex("HeLevel")));
            magmon.setWaterFlow1(cursor.getString(cursor.getColumnIndex("WaterFlow1")));
            magmon.setWaterTemp1(cursor.getString(cursor.getColumnIndex("WaterTemp1")));
            magmon.setLastTime(cursor.getString(cursor.getColumnIndex("LastTime")));
            magmon.setServerID(cursor.getInt(cursor.getColumnIndex("ServerID")));
            if (cursor.getInt(cursor.getColumnIndex("MonitoringEnabled")) == 1) {
                magmon.setMonitoringEnabled(true);
            } else {
                magmon.setMonitoringEnabled(false);
            }
            ArrayList<String> buflist2 = new ArrayList<String>();
            String buf = cursor.getString(cursor.getColumnIndex("Errors"));
            List<String> buflist = (List<String>) Arrays.asList(buf.split(","));
            for(String bufstr:buflist){
                buflist2.add(bufstr);
            }
            magmon.setErrors(buflist2);
            cursor.close();
        }catch (Exception e){
            MainActivity.print("update widget error db: "+e.getMessage());
        }
        return magmon;
    }

    public static MagMonServer getServerById(Context context, int ServerID){
        dbHelper = new DateBase(context);
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        MagMonServer server = new MagMonServer();
        try {
            Cursor cursor = userDB.query("servers", null, "id = ?", new String[]{Integer.toString(ServerID)},
                    null, null, null);
            cursor.moveToFirst();
            server.setName(cursor.getString(cursor.getColumnIndex("name")));
            server.setIP(cursor.getString(cursor.getColumnIndex("ip")));
            server.setPort(cursor.getString(cursor.getColumnIndex("port")));
            if(cursor.getInt(cursor.getColumnIndex("connect"))==1)
            {
                server.setConnect(true);
            }else{
                server.setConnect(false);
            }
            cursor.close();
        }catch (Exception e){
            MainActivity.print("update widget error db: "+e.getMessage());
        }
        return server;
    }

}
