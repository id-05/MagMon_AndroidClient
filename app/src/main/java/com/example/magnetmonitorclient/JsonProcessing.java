package com.example.magnetmonitorclient;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Set;

import static com.example.magnetmonitorclient.MainActivity.print;

public class JsonProcessing {

    public static DateBase dbHelper;

    public static void Processing(Context context, String str) throws InterruptedException {
        dbHelper = new DateBase(context);
        JsonParser parser = new JsonParser();
        JsonObject SourceJson=new JsonObject();
        SourceJson = parser.parse(str).getAsJsonObject();
        Set<String> keys = SourceJson.keySet();
        Object[] jsonkeys = keys.toArray();
        for (int i = 0; i <= jsonkeys.length - 1; i++) {
            JsonObject bufObject = SourceJson.getAsJsonObject(jsonkeys[i].toString());
            MagMonRec node = new MagMonRec();
            //print("jsonkeys["+i+"].toString() = "+jsonkeys[i].toString()+"  /"+jsonkeys.length);
            node.setName(jsonkeys[i].toString());
            if (bufObject.has("HePress")) node.setHePress(bufObject.get("HePress").getAsString());
            if (bufObject.has("HeLevel")) node.setHeLevel(bufObject.get("HeLevel").getAsString());
            if (bufObject.has("WT1")) node.setWaterTemp1(bufObject.get("WT1").getAsString());
            if (bufObject.has("WF1")) node.setWaterFlow1(bufObject.get("WF1").getAsString());
            if (bufObject.has("WT2")) node.setWaterTemp2(bufObject.get("WT2").getAsString());
            if (bufObject.has("WF2")) node.setWaterFlow2(bufObject.get("WF2").getAsString());
            if (bufObject.has("LastUpdate")) node.setLastTime(bufObject.get("LastUpdate").getAsString());
            if (bufObject.has("Errors")) {
                ArrayList<String> listError = new ArrayList<>();
                JsonArray bufArray = bufObject.getAsJsonArray("Errors");
                for(final JsonElement bufElement:bufArray){
                    listError.add(bufElement.toString());
                }
                node.setErrors(listError);
            }
                if(magmonUpdateBase(node)==0){
                    magmonAddBase(node);
                }
            Thread.sleep(1000);
        }
    }

    private static void refreshWidget(Context context) {
        Intent i = new Intent(MagMonClientWidget.FORCE_WIDGET_UPDATE);
        context.sendBroadcast(i);
    }

    public static void magmonAddBase(MagMonRec magmon){
        ContentValues newValues = new ContentValues();
        //print("add base "+magmon.Name);
        newValues.put("name",magmon.Name);
        newValues.put("HePress",magmon.getHePress());
        newValues.put("HeLevel",magmon.getHeLevel());
        newValues.put("WaterFlow1",magmon.getWaterFlow1());
        newValues.put("WaterTemp1",magmon.getWaterTemp1());
        newValues.put("WaterFlow2",magmon.getWaterFlow2());
        newValues.put("WaterTemp2",magmon.getWaterTemp2());
        newValues.put("Errors",magmon.Errors.size());
        newValues.put("LastTime",magmon.getLastTime());
        try {
            SQLiteDatabase userDB = dbHelper.getWritableDatabase();
            userDB.insertOrThrow("magmons", null, newValues);
            userDB.close();
        }catch (SQLException e){
            print("error add to base "+e.toString());
        }
    }

    public static int magmonUpdateBase(MagMonRec magmon) {
        ContentValues newValues = new ContentValues();
        newValues.put("HePress",magmon.getHePress());
        newValues.put("HeLevel",magmon.getHeLevel());
        newValues.put("WaterFlow1",magmon.getWaterFlow1());
        newValues.put("WaterTemp1",magmon.getWaterTemp1());
        newValues.put("WaterFlow2",magmon.getWaterFlow2());
        newValues.put("WaterTemp2",magmon.getWaterTemp2());
        newValues.put("Errors",magmon.Errors.size());
        newValues.put("LastTime",magmon.getLastTime());
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        int buf = userDB.update("magmons", newValues, "name = ?",
                new String[] {magmon.Name});
        return buf;
    }

}
