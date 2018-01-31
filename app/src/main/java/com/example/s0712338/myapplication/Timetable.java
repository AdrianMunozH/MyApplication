package com.example.s0712338.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Timetable extends TimetableBase {

    public TableLayout timetableLayout;
    public List<TableRow> rows;
    public int timetableRowCount;
    public int backgroundColor;
    public String[] days ={"Montag","Dienstag", "Mittwoch", "Donnerstag", "Freitag"};
    public HashMap<String, Integer> timetableSettings;
    public String ownerId = "";
    public String username = "";

    public String[] lessonTimes = new String[10];
    public Integer[] lessonHours = new Integer[10];
    public Integer[] lessonMins = new Integer[10];

    public Timetable(Context context, TableLayout timetableLayout, HashMap<String, Integer> settings,
                     String saveFile, String username, String deviceId) {
        this.context = context;
        this.timetableLayout = timetableLayout;
        super.file = this.file = saveFile;
        this.rows = new ArrayList<TableRow>();
        this.timetableSettings = settings;
        this.username = username;

        this.encoding = "UTF-8";
        this.timetableRowCount = 5;
        this.backgroundColor = Color.LTGRAY;

        this.ownerId = deviceId;
        this.load();
        if ( this.ownerId.equals("") ) {
            this.ownerId = this.getUniqueUserId();
        }
    }

    public void buildTimetableLayout() {
        this.destroyTimetableLayout();
        this.calculateLessonTimes();
        // TODO: check if lessonTimes and timetablesettings are set
        for ( int rowNumber = 0; rowNumber <= this.timetableSettings.get("lastClass"); rowNumber++ ) {
            TableRow newRow = new TableRow(context);

            // Add lesson time to row
            TextView lessonTimeTextView = new TextView(context);
            lessonTimeTextView.setPadding(20, 5, 20, 5);
            if (rowNumber > 0) {
                lessonTimeTextView.setText(this.lessonTimes[rowNumber-1]);
            }
            newRow.addView(lessonTimeTextView);

            // Add edit text widgets for lessons to row
            for (int columnNumber = 1; columnNumber <= days.length; columnNumber++) {
                if (rowNumber == 0) {
                    TextView dayTextView = new TextView(context);
                    dayTextView.setPadding(20, 5, 20, 5);
                    dayTextView.setText(days[columnNumber-1]);
                    newRow.addView(dayTextView);
                    dayTextView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    dayTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    EditText lessonTextEdit = new EditText(context);
                    newRow.addView(lessonTextEdit);
                    lessonTextEdit.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    lessonTextEdit.setGravity(Gravity.CENTER_HORIZONTAL);
                    lessonTextEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                    lessonTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            boolean handled = false;
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                Toast.makeText(context, "Gespeichert", Toast.LENGTH_LONG).show();
                                save();
                                handled = true;
                            }
                            return handled;
                        }
                    });
                }
            }
            timetableLayout.addView(newRow);
            rows.add(newRow);

            // Set layout params of row
            TableLayout.LayoutParams timetableLayoutParams = (TableLayout.LayoutParams) newRow.getLayoutParams();
            timetableLayoutParams.leftMargin = 30;
            timetableLayoutParams.rightMargin = 30;
            newRow.setLayoutParams(timetableLayoutParams);

            // set background colour for every second row
            if ( rowNumber != 0 && rowNumber % 2 == 0 ) {
                newRow.setBackgroundColor(this.backgroundColor);
            }
        }
    }

    public void destroyTimetableLayout() {
        this.timetableLayout.removeAllViews();
        this.rows.clear();
    }

    public JSONObject timetableSettingsToJson() throws JSONException {
        JSONObject timetableSettingsJson = new JSONObject();

        Set<String> keys = this.timetableSettings.keySet();
        for(String key : keys) {
            timetableSettingsJson.put(key, timetableSettings.get(key));
        }

        Log.d("Timetable", "Timetable settings converted to json");
        Log.d("Timetable", timetableSettingsJson.toString(4));

        return timetableSettingsJson;
    }

    public JSONArray timetableDataToJson() {
        JSONArray timetable = new JSONArray();

        for (int i = 1; i < timetableLayout.getChildCount(); i++) {
            View child = timetableLayout.getChildAt(i);
            JSONArray timetableRow = new JSONArray();

            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;

                for (int j = 1; j < row.getChildCount(); j++) {
                    TextView cell = (TextView) row.getChildAt(j);
                    timetableRow.put(cell.getText().toString());
                }
            }
            timetable.put(timetableRow);
        }
        return timetable;
    }

    public JSONObject toJson() {
        JSONObject timetableJson = new JSONObject();
        try {
            timetableJson = this.timetableSettingsToJson();

            timetableJson.put("identifier", this.ownerId);
            timetableJson.put("username", this.username);

            JSONArray timetableDataJson = this.timetableDataToJson();
            timetableJson.put("data", timetableDataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return timetableJson;
    }

    public void save() {
        try {
            Log.i("Save", "Save process started");
            Log.i("Save", "saving to " + this.file);

            JSONObject timetableJson = this.toJson();
            this.writeToFile(timetableJson.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (this.timetableSettings.get("sync") > 0) {
            this.syncWithTelegramBot();
        }
    }

    public void syncWithTelegramBot() {
        new Thread(
                new MailSendingThread(context, this.file)
        ).start();
    }

    public void load() {
        try {
            String timetableString = this.loadFromFile();
            if ( timetableString != "" ) {
                this.update(timetableString);
            } else {
                this.buildTimetableLayout();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void update(String timetableString) throws JSONException {
        JSONObject timetableJson = new JSONObject(timetableString);

        for (Iterator<String> keyIterator = timetableJson.keys(); keyIterator.hasNext(); ) {
            String key = keyIterator.next();
            if (!key.equals("data") && !key.equals("identifier") && !key.equals("username")) {
                this.timetableSettings.put(key, timetableJson.getInt(key));
            }
        }

        this.destroyTimetableLayout();
        this.buildTimetableLayout();

        if (timetableJson.has("identifier")) {
            this.ownerId = timetableJson.get("identifier").toString();
        }
        if (timetableJson.has("username")) {
            this.username = timetableJson.get("username").toString();
        }

        JSONArray timetableData = timetableJson.getJSONArray("data");

        int hours = this.timetableSettings.get("lastClass") - this.timetableSettings.get("firstClass") + 1;
        for ( int i = 1; i <= hours; i++ ) {
            JSONArray timetableRowJson = timetableData.getJSONArray(i-1);
            TableRow timetableRow = rows.get(i);

            for ( int j = 1; j < timetableRow.getChildCount(); j++ ) {
                String lesson = timetableRowJson.getString(j-1);
                EditText cell = (EditText) timetableRow.getChildAt(j);
                cell.setText(lesson);
            }
        }
    }

    public void calculateLessonTimes() {
        Log.d("InitLessons", "start calculating");

        lessonHours[0] = this.timetableSettings.get("startHour");
        lessonMins[0] = this.timetableSettings.get("startMin");
        lessonTimes[0] = lessonHours[0].toString() + ":" + lessonMins[0].toString();

        for (int i = 1; i < this.timetableSettings.get("lastClass"); i++) {
            Integer hDif = ((this.timetableSettings.get("length") + this.timetableSettings.get("break"))) / 60;
            Integer mDif = ((this.timetableSettings.get("length") + this.timetableSettings.get("break"))) % 60;
            hDif = (lessonMins[i-1] + mDif >= 60) ? hDif + 1 : hDif;
            mDif = (lessonMins[i-1] + mDif >= 60) ? mDif - 60 : mDif;
            lessonHours[i]=lessonHours[i-1]+hDif;
            lessonMins[i]=lessonMins[i-1]+mDif;

            if ( lessonMins[i] < 10 ) {
                lessonTimes[i] = lessonHours[i].toString() + ":0" + lessonMins[i].toString();
            } else {
                lessonTimes[i] = lessonHours[i].toString() + ":" + lessonMins[i].toString();
            }
        }

        Log.d("InitLessons", Arrays.toString(lessonTimes));
    }

    @SuppressLint("HardwareIds")
    public String getUniqueUserId() {
        return Settings.Secure.getString(this.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
