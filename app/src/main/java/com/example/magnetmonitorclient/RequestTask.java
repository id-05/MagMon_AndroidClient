package com.example.magnetmonitorclient;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import static com.example.magnetmonitorclient.MainActivity.print;
import static java.nio.charset.StandardCharsets.UTF_8;

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
            try{
                //String auth =new String( "" + "");
                //byte[] data1 = auth.getBytes(UTF_8);
                //String base64 = android.util.Base64.encodeToString(data1, android.util.Base64.NO_WRAP);

                try {
                    URL url = new URL("http://"+server.getIP()+":"+server.getPort()+"/json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(1000);
                    connection.connect();
                    //JSONObject obj = new JSONObject();
                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "windows-1251"));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        bufferedReader.close();
                        String result = sb.toString();
                        String new_str = result.replace ("null", "----");
                        //print( "answer =  "+result);
                        JsonProcessing.Processing(context,new_str);
                        refreshWidget(context);
                    }else {
                        print("error 2  "+ String.valueOf(responseCode));
                    }
                }catch (Exception e) {
                    print("error 3= "+e.toString());
                }
            }catch (Exception e){
                print("error 1 "+e.toString());
            }
        }
    }

    private static void refreshWidget(Context context) {
        Intent i = new Intent(MagMonClientWidget.FORCE_WIDGET_UPDATE);
        context.sendBroadcast(i);
        print("send command update");
    }
}
