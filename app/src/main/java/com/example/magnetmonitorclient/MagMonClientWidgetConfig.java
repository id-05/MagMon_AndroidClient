package com.example.magnetmonitorclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import java.util.ArrayList;

public class MagMonClientWidgetConfig extends AppCompatActivity {

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;
    Spinner spinnerMagMon;

    public static DateBase dbHelper;
    private ArrayList<String> MagMonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag_mon_client_widget_config);
        // извлекаем ID конфигурируемого виджета
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // и проверяем его корректность
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // формируем intent ответа
          resultValue = new Intent();
          resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
          ArrayList<String> MagMonNameList = GetMagMonList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MagMonNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMagMon = (Spinner) findViewById(R.id.spinner1);
        spinnerMagMon.setAdapter(adapter);

         //отрицательный ответ
        setResult(RESULT_CANCELED, resultValue);

        Button but = findViewById(R.id.button);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("widget_pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("widget_" + widgetID, spinnerMagMon.getSelectedItem().toString());
                editor.commit();

                //final ComponentName serviceName = new ComponentName(, MagMonService.class);
                //Intent intent = new Intent();
                //intent.setComponent(serviceName);

                setResult(RESULT_OK, resultValue);
                //startService(new Intent(MagMonClientWidgetConfig.this,MagMonService.class));
                MainActivity.print("add new widget");
                finish();
            }
        });

    }

    public ArrayList<String> GetMagMonList(){
        dbHelper = new DateBase(this);
        MagMonList.clear();
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = userDB.query("magmons", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    //MainActivity.print("cursor.getString(cursor.getColumnIndex(\"name\")) = "+cursor.getString(cursor.getColumnIndex("name")));
                    MagMonList.add(cursor.getString(cursor.getColumnIndex("name")));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            MainActivity.print("error "+e.toString());
        }
        return MagMonList;
    }
}