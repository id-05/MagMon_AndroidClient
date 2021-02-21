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

    public static void Processing(Context context, String str, int ServerId) throws InterruptedException {
        dbHelper = new DateBase(context);
        JsonParser parser = new JsonParser();
        JsonObject SourceJson=new JsonObject();
        SourceJson = parser.parse(str).getAsJsonObject();
        Set<String> keys = SourceJson.keySet();
        Object[] jsonkeys = keys.toArray();
        for (int i = 0; i <= jsonkeys.length - 1; i++) {
            JsonObject bufObject = SourceJson.getAsJsonObject(jsonkeys[i].toString());
            MagMonRec node = new MagMonRec();
            node.setName(jsonkeys[i].toString());
            node.setServerID(ServerId);
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
                if(bufArray.size()!=0){
                    for(final JsonElement bufElement:bufArray){
                        listError.add(bufElement.toString());
                    }
                }else{listError.add("OK");
                    }
                node.setErrors(listError);
            }
            if (bufObject.has("HeLevelCurrent")) node.setHeLevelCurrent(bufObject.get("HeLevelCurrent").getAsString());
            if (bufObject.has("HeLevelTopCurrent")) node.setHeLevelTopCurrent(bufObject.get("HeLevelTopCurrent").getAsString());
            if (bufObject.has("HeLevelTop")) node.setHeLevelTop(bufObject.get("HeLevelTop").getAsString());
            if (bufObject.has("ReconRuOCurrent")) node.setReconRuOCurrent(bufObject.get("ReconRuOCurrent").getAsString());
            if (bufObject.has("ReconRuO")) node.setReconRuO(bufObject.get("ReconRuO").getAsString());
            if (bufObject.has("SpareScanRoom1A")) node.setSpareScanRoom1A(bufObject.get("SpareScanRoom1A").getAsString());
            if (bufObject.has("ShieldTempCurrent")) node.setShieldTempCurrent(bufObject.get("ShieldTempCurrent").getAsString());
            if (bufObject.has("ShieldTemp")) node.setShieldTemp(bufObject.get("ShieldTemp").getAsString());
            if (bufObject.has("ReconSi410Current")) node.setReconSi410Current(bufObject.get("ReconSi410Current").getAsString());
            if (bufObject.has("ReconSi410")) node.setReconSi410(bufObject.get("ReconSi410").getAsString());
            if (bufObject.has("SpareScanRoom1B")) node.setSpareScanRoom1B(bufObject.get("SpareScanRoom1B").getAsString());
            if (bufObject.has("ColdheadRuOCurrent")) node.setColdheadRuOCurrent(bufObject.get("ColdheadRuOCurrent").getAsString());
            if (bufObject.has("ColdheadTemp")) node.setColdheadTemp(bufObject.get("ColdheadTemp").getAsString());
            if (bufObject.has("SCPressure")) node.setSCPressure(bufObject.get("SCPressure").getAsString());
            if (bufObject.has("SpareCmp1b")) node.setSpareCmp1b(bufObject.get("SpareCmp1b").getAsString());
            if (bufObject.has("SpareCmp1c")) node.setSpareCmp1c(bufObject.get("SpareCmp1c").getAsString());
            if (bufObject.has("ReconSi4102a")) node.setReconSi4102a(bufObject.get("ReconSi4102a").getAsString());
            if (bufObject.has("ReconSi4102aCurrent")) node.setReconSi4102aCurrent(bufObject.get("ReconSi4102aCurrent").getAsString());
            if (bufObject.has("ReconSi4102b")) node.setReconSi4102b(bufObject.get("ReconSi4102b").getAsString());
            if (bufObject.has("ReconSi4102bCurrent")) node.setReconSi4102bCurrent(bufObject.get("ReconSi4102bCurrent").getAsString());
            if (bufObject.has("VoltsPlusExternal")) node.setVoltsPlusExternal(bufObject.get("VoltsPlusExternal").getAsString());
            if (bufObject.has("VoltsPlus")) node.setVoltsPlus(bufObject.get("VoltsPlus").getAsString());
            if (bufObject.has("VoltsMinus")) node.setVoltsMinus(bufObject.get("VoltsMinus").getAsString());
            if (bufObject.has("VoltsMinusExternal")) node.setVoltsMinusExternal(bufObject.get("VoltsMinusExternal").getAsString());
            if (bufObject.has("HFOBottomShield")) node.setHFOBottomShield(bufObject.get("HFOBottomShield").getAsString());
            if (bufObject.has("MagmonCaseTemp")) node.setMagmonCaseTemp(bufObject.get("MagmonCaseTemp").getAsString());

                if(magmonUpdateBase(node)==0){
                    magmonAddBase(node);
                }
                //MainActivity.adapter.notifyDataSetChanged();
            Thread.sleep(1000);
        }
    }

    private static void refreshWidget(Context context) {
        Intent i = new Intent(MagMonClientWidget.FORCE_WIDGET_UPDATE);
        context.sendBroadcast(i);
    }

    public static void magmonAddBase(MagMonRec magmon){
        ContentValues newValues = new ContentValues();
        newValues.put("name",magmon.Name);
        newValues.put("ServerID",magmon.getServerID());
        newValues.put("HePress",magmon.getHePress());
        newValues.put("HeLevel",magmon.getHeLevel());
        newValues.put("WaterFlow1",magmon.getWaterFlow1());
        newValues.put("WaterTemp1",magmon.getWaterTemp1());
        newValues.put("WaterFlow2",magmon.getWaterFlow2());
        newValues.put("WaterTemp2",magmon.getWaterTemp2());
        //print(Integer.toString(magmon.getErrors().size()));
        String buf = magmon.getErrors().get(0);
        for(int i=0; i>=magmon.getErrors().size();i++){
            buf = buf + "," + magmon.getErrors().get(i);
        }
        print("buf "+buf);
        newValues.put("Errors",buf);
        newValues.put("LastTime",magmon.getLastTime());
        newValues.put("MonitoringEnabled",1);
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
        newValues.put("ServerID",magmon.getServerID());
        newValues.put("HePress",magmon.getHePress());
        newValues.put("HeLevel",magmon.getHeLevel());
        newValues.put("WaterFlow1",magmon.getWaterFlow1());
        newValues.put("WaterTemp1",magmon.getWaterTemp1());
        newValues.put("WaterFlow2",magmon.getWaterFlow2());
        newValues.put("WaterTemp2",magmon.getWaterTemp2());
        String strbuf = magmon.getErrors().get(0);
        for(int i=1; i<magmon.getErrors().size(); i++){
            strbuf = strbuf + "," + magmon.getErrors().get(i);
        }
        newValues.put("Errors",strbuf);

        newValues.put("LastTime",magmon.getLastTime());
        SQLiteDatabase userDB = dbHelper.getWritableDatabase();
        int buf = userDB.update("magmons", newValues, "name = ?",
                new String[] {magmon.Name});
        userDB.close();
        return buf;
    }

}
