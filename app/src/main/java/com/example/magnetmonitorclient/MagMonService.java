package com.example.magnetmonitorclient;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.view.View;

import androidx.core.app.NotificationCompat;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.magnetmonitorclient.MainActivity.print;

public class MagMonService extends Service {

    private Timer mainTimer;
    private ArrayList<MagMonServer> ServerList = new ArrayList<>();
    private RequestTask mainTask;
    //public static DateBase dbHelper;

    public MagMonService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mainTimer = new Timer();
        ServerList = MainActivity.getServerList(this);
        if (ServerList.size()>0) {
            mainTask = new RequestTask(this,ServerList);
            mainTimer.schedule(mainTask, 0,2*60*1000);
        }

        Notification notification;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"1").
                setSmallIcon(R.drawable.ikonka);
        if (android.os.Build.VERSION.SDK_INT<=15) {
            notification = builder.getNotification(); // API-15 and lower
        }else{
            notification = builder.build();
        }
        startForeground(777, notification);
        //return super.onStartCommand(intent, flags, startId);
        //return START_STICKY;
        return START_REDELIVER_INTENT;
    }
}
