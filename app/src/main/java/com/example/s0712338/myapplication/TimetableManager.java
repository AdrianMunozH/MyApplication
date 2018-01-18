package com.example.s0712338.myapplication;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class TimetableManager {
    public Context context;
    public String configFile;
    public Timetable currentTimetable;
    public TableLayout timetableLayout;
    public HashMap<String, Integer> timetableSettings;
    public String username;

    public ArrayList<HashMap<String, String>> allTimetables = new ArrayList();

    public String encoding;
    public String logTag;

    public TimetableManager(Context context, TableLayout timetableLayout,
                            String configFile, HashMap<String, Integer> timetableSettings,
                            String username) {
        this.context = context;
        this.timetableLayout = timetableLayout;
        this.configFile = configFile;
        this.timetableSettings = timetableSettings;
        this.username = username;

        this.encoding = "UTF-8";
        this.load();
        this.logTag = "TimetableManager";
    }

    public void addTimetable(String file, Timetable timetable) {
        Log.d(this.logTag, "add timetable");
    }

    public void removeTimetable(String id) {
        Log.d(this.logTag, "remove timetable");
    }

    // returns timetable of device owner; decides using unique id
    public Timetable getMyTimetable() {
        String myDeviceId = this.getUniqueUserId();
        String file = this.getTimetableFilename(myDeviceId);
        return new Timetable(this.context, this.timetableLayout, this.timetableSettings, file, this.username, myDeviceId);
    }

    /* public Timetable getTimetable(String id) {
        return;
    } */

    public String getUniqueUserId() {
        return Settings.Secure.getString(this.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    /*public String[] getList() {


    }*/

    public JSONArray toJson() {
        JSONArray json = new JSONArray();
        try {
            for (HashMap<String, String> entry: this.allTimetables) {
                if (entry.containsKey("deviceId") && entry.containsKey("username")) {
                    JSONObject timetableEntry = new JSONObject();
                    timetableEntry.put("deviceId", entry.get("deviceId"));
                    timetableEntry.put("username", entry.get("username"));
                    json.put(timetableEntry);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void save() {
        Log.i("Save", "Save process started");
        try {
            FileOutputStream fos = context.openFileOutput(this.configFile, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, this.encoding);

            JSONArray timetable = this.toJson();
            Log.i("Save", timetable.toString(4));

            osw.write(timetable.toString());
            osw.close();

            Toast.makeText(this.context, "Erfolgreich gespeichert", Toast.LENGTH_LONG).show();
            Log.i("Save", "Save process finished");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            String configJson = this.loadFromFile();
            if ( !configJson.isEmpty() ) {
                JSONArray timetablesJson = new JSONArray(configJson);
                this.allTimetables.clear();
                for ( int i = 0; i < timetablesJson.length(); i++ ) {
                    JSONObject timetableEntryJSON = timetablesJson.getJSONObject(i);
                    HashMap<String, String>  timetableEntry = new HashMap<>();
                    timetableEntry.put("username", timetableEntryJSON.getString("username"));
                    timetableEntry.put("deviceId", timetableEntryJSON.getString("deviceId"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadFromFile() {
        FileInputStream fis;
        StringBuilder sb = new StringBuilder();
        try {
            fis = this.context.openFileInput(this.configFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, this.encoding));
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getTimetableFilename(String deviceId) {
        return deviceId + ".json";
    }
}
