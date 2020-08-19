package com.example.magnetmonitorclient;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimerTask;
import javax.net.ssl.HttpsURLConnection;
import static com.example.magnetmonitorclient.MainActivity.print;

public class RequestTask extends TimerTask {

    ArrayList<MagMonServer> ServerList;
    Context context;

    public RequestTask(Context context, ArrayList<MagMonServer> ServerList){
        this.ServerList = ServerList;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        for (final MagMonServer server:ServerList){
                try {
                    URL url = new URL("http://"+server.getIP()+":"+server.getPort()+"/json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(1000);
                    connection.connect();
                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        server.setConnect(true);
                        MainActivity.serverUpdateBase(server);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "windows-1251"));
                        String line = null;
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        bufferedReader.close();
                        String result = stringBuilder.toString();
                        String new_str = result.replace ("null", "----");
                        JsonProcessing.Processing(context,new_str,server.getId());
                        refreshWidget(context);
                    }else {
                        print("error response code  "+ String.valueOf(responseCode));
                    }
                }catch (Exception e) {
                    print("error connect "+e.toString());
                    server.setConnect(false);
                    MainActivity.serverUpdateBase(server);
                    refreshWidget(context);
                }
        }
    }

    private static void refreshWidget(Context context) {
        Intent i = new Intent(MagMonClientWidget.FORCE_WIDGET_UPDATE);
        context.sendBroadcast(i);
        print("send command update");
    }
}
