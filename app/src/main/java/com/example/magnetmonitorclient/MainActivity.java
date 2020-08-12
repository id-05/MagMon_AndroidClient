package com.example.magnetmonitorclient;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;
import static java.nio.charset.StandardCharsets.UTF_8;

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

    public static void serverUpdateBase(MagMonServer server) {
        int id = server.getId();
        ContentValues newValues = new ContentValues();
        newValues.put("name",server.getName());
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        userDB.update("servers", newValues, "id = ?",
                new String[] {String.valueOf(id)});
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
                    server.Name = (cursor.getString(cursor.getColumnIndex("name")));
                    server.IP = (cursor.getString(cursor.getColumnIndex("ip")));
                    server.Port = (cursor.getString(cursor.getColumnIndex("port")));
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
                    magmon.setName(cursor.getString(cursor.getColumnIndex("name")));
                    magmon.setHePress(cursor.getString(cursor.getColumnIndex("HePress")));
                    magmon.setHeLevel(cursor.getString(cursor.getColumnIndex("HeLevel")));
                    magmon.setWaterFlow1(cursor.getString(cursor.getColumnIndex("WaterFlow1")));
                    magmon.setWaterTemp1(cursor.getString(cursor.getColumnIndex("WaterTemp1")));
                    magmon.setLastTime(cursor.getString(cursor.getColumnIndex("LastTime")));
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

}