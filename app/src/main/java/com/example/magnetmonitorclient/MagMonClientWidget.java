package com.example.magnetmonitorclient;

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

import java.util.ArrayList;

public class MagMonClientWidget extends AppWidgetProvider {

    public static String FORCE_WIDGET_UPDATE = "com.example.magnetmonitorclient.FORCE_WIDGET_UPDATE";
    public static DateBase dbHelper;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "widget_pref", Context.MODE_PRIVATE);
        //appWidgetManager = AppWidgetManager.getInstance(context);
        //ComponentName thisWidget = new ComponentName(context, MagMonClientWidget.class);
        MainActivity.print("upDate");
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.mag_mon_client_widget);
        widgetView.setImageViewResource(R.id.mriImage,R.drawable.front_gray);
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
        //MainActivity.print("appWidgetIds = "+appWidgetIds.toString());
        if (FORCE_WIDGET_UPDATE.equals(intent.getAction()))
        {
            for (int appWidgetId : appWidgetIds) {
                updateWidget(context,appWidgetManager,sharedPreferences,appWidgetId);
            }
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                             SharedPreferences sharedPreferences, int widgetID) {
        // Читаем параметры Preferences
        String widgetMagMon = sharedPreferences.getString("widget_" + widgetID, null);
        if (widgetMagMon == null) return;
        // Настраиваем внешний вид виджета

        MagMonRec magmon = new MagMonRec();
        magmon = MainActivity.getMagMonByName(context,widgetMagMon);
        MagMonServer server = new MagMonServer();
        server = MainActivity.getServerById(context,magmon.getServerID());
        RemoteViews widgetView;
        if(!server.getConnect()){
            //client not connect to server
            MainActivity.print("no connect");
            widgetView = new RemoteViews(context.getPackageName(), R.layout.mag_mon_client_widget_noconnect);
            widgetView.setTextViewText(R.id.nameMagnet, widgetMagMon);
            widgetView.setTextViewText(R.id.statusString,"Client is not");
            widgetView.setImageViewResource(R.id.mriImage, R.drawable.front_noconnect);
        }else {
            if (magmon.getHePress().equals("----")) {
                //magmon is not connect to seerver
                widgetView = new RemoteViews(context.getPackageName(), R.layout.mag_mon_client_widget_noconnect);
                widgetView.setImageViewResource(R.id.mriImage, R.drawable.front_gray);
                widgetView.setTextViewText(R.id.nameMagnet, widgetMagMon);
                widgetView.setTextViewText(R.id.statusString, "MagMon is not");
            }else{

            widgetView = new RemoteViews(context.getPackageName(), R.layout.mag_mon_client_widget);
            widgetView.setTextViewText(R.id.nameMagnet, widgetMagMon);
            widgetView.setTextViewText(R.id.w_hepress, magmon.getHePress());
            widgetView.setTextViewText(R.id.w_helevel, magmon.getHeLevel() + "%");
            widgetView.setTextViewText(R.id.w_wf, magmon.getWaterFlow1());
            widgetView.setTextViewText(R.id.w_wt, magmon.getWaterTemp1());
            if (magmon.getErrors().size() == 1) {
                widgetView.setImageViewResource(R.id.mriImage, R.drawable.front_yellow);
            } else {
                widgetView.setImageViewResource(R.id.mriImage, R.drawable.front);
            }

            if (magmon.getMonitoringEnabled()) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context, "1")
                                .setSmallIcon(R.drawable.ikonka)
                                .setContentTitle(magmon.getName())
                                .setContentText("Error: " + magmon.getErrors().get(0) + "\n")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setSound(alarmSound)
                                .setLights(0xff00ff00, 2000, 500)
                                .setColorized(true)
                                .setAutoCancel(true); // автоматически закрыть уведомление после нажатия
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(777, builder.build());
            }

            widgetView.setTextViewText(R.id.w_time, magmon.getLastTime());
        }
        }
        // Обновление виджета (вторая зона)
        final ComponentName serviceName = new ComponentName(context, MagMonService.class);
        Intent intent = new Intent();
        intent.setComponent(serviceName);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        widgetView.setOnClickPendingIntent(R.id.mriImage, pendingIntent);
        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetID, widgetView);
        MainActivity.print("update ok");
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

