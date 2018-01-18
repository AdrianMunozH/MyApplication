package com.example.s0712338.myapplication;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class TimetableManager extends TimetableBase {
    public Context context;
    public String file;
    public TableLayout timetableLayout;
    public HashMap<String, Integer> timetableSettings;
    public String username;

    public ArrayList<HashMap<String, String>> allTimetables = new ArrayList();

    public String logTag;

    public TimetableManager(Context context, TableLayout timetableLayout,
                            String configFile, HashMap<String, Integer> timetableSettings,
                            String username) {
        super.context = this.context = context;
        super.file = this.file = configFile;
        this.timetableLayout = timetableLayout;
        this.timetableSettings = timetableSettings;
        this.username = username;

        this.load();
        this.logTag = "TimetableManager";
    }

    public void addTimetable(Timetable timetable) {
        HashMap<String, String> newTimetableEntry = new HashMap();

        newTimetableEntry.put("username", timetable.username);
        newTimetableEntry.put("deviceId", timetable.ownerId);

        this.allTimetables.add(newTimetableEntry);
    }

    public void removeTimetable(String id) {
        Log.d(this.logTag, "remove timetable");
    }

    // returns timetable of device owner; decides using unique id
    public Timetable getMyTimetable() {
        String myDeviceId = this.getUniqueUserId();
        try {
            return this.getTimetable(myDeviceId);
        } catch (NoSuchElementException e) {
            Timetable timetable = new Timetable(this.context, this.timetableLayout, this.timetableSettings,
                    this.getTimetableFilename(myDeviceId), this.username, myDeviceId);
            this.addTimetable(timetable);
            return timetable;
        }
    }

    public Timetable getTimetable(String searchValue) {
        if ( this.allTimetables.size() > 0 ) {
            for (HashMap<String, String> timetableEntry : this.allTimetables) {
                if (timetableEntry.get("deviceId").equals(searchValue) || timetableEntry.get("username").equals(searchValue)) {
                    String id = timetableEntry.get("deviceId");
                    String username = timetableEntry.get("username");
                    return new Timetable(this.context, this.timetableLayout, this.timetableSettings,
                            this.getTimetableFilename(id), username, id);
                }
            }
        }
        throw new NoSuchElementException();
    }

    public String getUniqueUserId() {
        return Settings.Secure.getString(this.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

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
        try {
            Log.i("Save", "Save process started");

            JSONArray timetableManagerJson = this.toJson();
            this.writeToFile(timetableManagerJson.toString(4));

            Toast.makeText(this.context, "Erfolgreich gespeichert", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
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
                    this.allTimetables.add(timetableEntry);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTimetableFilename(String deviceId) {
        return deviceId + ".json";
    }
}
