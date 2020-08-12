package com.example.magnetmonitorclient;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.magnetmonitorclient.MainActivity.print;

/**
 * Implementation of App Widget functionality.
 */
public class MagMonClientWidget extends AppWidgetProvider {

    public static String FORCE_WIDGET_UPDATE = "com.example.magnetmonitorclient.FORCE_WIDGET_UPDATE";
    public static DateBase dbHelper;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "widget_pref", Context.MODE_PRIVATE);
        for (int appWidgetId : appWidgetIds) {
                updateWidget(context, appWidgetManager, sharedPreferences, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "widget_pref", Context.MODE_PRIVATE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, MagMonClientWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        if (FORCE_WIDGET_UPDATE.equals(intent.getAction()))
        {
            for (int appWidgetId : appWidgetIds) {
                updateWidget(context,appWidgetManager,sharedPreferences,appWidgetId);
            }
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                             SharedPreferences sp, int widgetID) {
        // Читаем параметры Preferences
        String widgetMagMon = sp.getString("widget_" + widgetID, null);
        if (widgetMagMon == null) return;

        // Настраиваем внешний вид виджета
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.mag_mon_client_widget);
        widgetView.setTextViewText(R.id.nameMagnet, widgetMagMon);
        widgetView.setImageViewResource(R.id.imageView1,R.drawable.front);

        dbHelper = new DateBase(context);
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = userDB.query("magmons",
                    null,
                    "name = ?",
                    new String[] {widgetMagMon},
                    null, null, null);
            cursor.moveToFirst();
            widgetView.setTextViewText(R.id.w_hepress,cursor.getString(cursor.getColumnIndex("HePress")));
            widgetView.setTextViewText(R.id.w_helevel,cursor.getString(cursor.getColumnIndex("HeLevel"))+"%");
            widgetView.setTextViewText(R.id.w_wf,cursor.getString(cursor.getColumnIndex("WaterFlow1")));
            widgetView.setTextViewText(R.id.w_wt,cursor.getString(cursor.getColumnIndex("WaterTemp1")));
//            SimpleDateFormat formatForDateNow = new SimpleDateFormat("HH:mm");
//            Date currentDate = new Date();
            //widgetView.setTextViewText(R.id.w_time,formatForDateNow.format(currentDate));
            //print(formatForDateNow.format(currentDate));
            String buf =cursor.getString(cursor.getColumnIndex("Errors"));
            if(buf.equals("1")){
                widgetView.setImageViewResource(R.id.imageView1,R.drawable.front_yellow);
            }
            if(cursor.getString(cursor.getColumnIndex("HePress")).equals("----")){
                widgetView.setImageViewResource(R.id.imageView1,R.drawable.front_gray);
            }
            //widgetView.setTextViewText(R.id.w_time,cursor.getString(cursor.getColumnIndex("Errors")));
            widgetView.setTextViewText(R.id.w_time,cursor.getString(cursor.getColumnIndex("LastTime")));
            cursor.close();
        }catch (SQLException e){
            print("update widget error: "+e.getMessage().toString());
        }
        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetID, widgetView);
        print("update ok");
    }



    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

