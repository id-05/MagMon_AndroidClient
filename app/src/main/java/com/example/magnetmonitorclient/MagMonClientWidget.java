package com.example.magnetmonitorclient;

import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
        widgetView.setImageViewResource(R.id.mriImage,R.drawable.front);

        dbHelper = new DateBase(context);
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = userDB.query("magmons", null, "name = ?", new String[] {widgetMagMon},
                    null, null, null);
            cursor.moveToFirst();
            widgetView.setTextViewText(R.id.w_hepress,cursor.getString(cursor.getColumnIndex("HePress")));
            widgetView.setTextViewText(R.id.w_helevel,cursor.getString(cursor.getColumnIndex("HeLevel"))+"%");
            widgetView.setTextViewText(R.id.w_wf,cursor.getString(cursor.getColumnIndex("WaterFlow1")));
            widgetView.setTextViewText(R.id.w_wt,cursor.getString(cursor.getColumnIndex("WaterTemp1")));
            String buf =cursor.getString(cursor.getColumnIndex("Errors"));
            if(buf.equals("1")){
                widgetView.setImageViewResource(R.id.mriImage,R.drawable.front_yellow);
                Uri alarmSound =
                        RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context, "1")
                                .setSmallIcon(R.drawable.ikonka)
                                .setContentTitle("Warning")
                                .setContentText("MagMon "+cursor.getString(cursor.getColumnIndex("name"))+" have problem /n")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setSound(alarmSound)
                                .setLights(0xff00ff00,2000,500)
                                //.set
                                //.setContentIntent(contentIntent)
                                // необязательные настройки

                                //.setTicker("Последнее китайское предупреждение!") // до Lollipop
                                .setAutoCancel(true); // автоматически закрыть уведомление после нажатия
                //notification.flags = Notification.FLAG_SHOW_LIGHTS;
                //builder.getNotification().flags= Notification.FLAG_SHOW_LIGHTS;
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(context);
                notificationManager.notify(777, builder.build());
            }
            if(cursor.getString(cursor.getColumnIndex("HePress")).equals("----")){
                widgetView.setImageViewResource(R.id.mriImage,R.drawable.front_gray);
            }
            widgetView.setTextViewText(R.id.w_time,cursor.getString(cursor.getColumnIndex("LastTime")));
            cursor.close();
        }catch (SQLException e){
            print("update widget error: "+e.getMessage().toString());
        }

        // Обновление виджета (вторая зона)
        Intent configIntent = new Intent(context, MainActivity.class);

        final ComponentName serviceName = new ComponentName(context, MagMonService.class);
        Intent intent = new Intent();
        intent.setComponent(serviceName);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        widgetView.setOnClickPendingIntent(R.id.mriImage, pendingIntent);
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

