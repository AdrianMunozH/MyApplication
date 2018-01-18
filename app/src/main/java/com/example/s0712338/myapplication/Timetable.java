package com.example.s0712338.myapplication;

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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Timetable {

    public TableLayout timetableLayout;
    public List<TableRow> rows;
    public String saveFile;
    public String encoding;
    public Context context;
    public int timetableRowCount;
    public int backgroundColor;
    public String[] days ={"Montag","Dienstag", "Mittwoch", "Donnerstag", "Freitag"};
    public HashMap<String, Integer> timetableSettings;
    public String ownerId = "";
    public String username = "";

    public String[] lessonTimes = new String[10];
    public Integer[] lessonHours = new Integer[10];
    public Integer[] lessonMins = new Integer[10];

    public Timetable(Context context, TableLayout timetableLayout, String saveFile,
                     HashMap<String, Integer> timetableSettings, String username) {
        this.context = context;
        this.timetableLayout = timetableLayout;
        this.saveFile = saveFile;
        this.rows = new ArrayList<TableRow>();
        this.timetableSettings = timetableSettings;
        this.username = username;

        this.encoding = "UTF-8";
        this.timetableRowCount = 5;
        this.backgroundColor = Color.LTGRAY;

        this.loadTimetable();
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

    public JSONObject toJson() throws JSONException {
        JSONObject timetableJson = this.timetableSettingsToJson();
        JSONArray timetableDataJson = this.timetableDataToJson();

        timetableJson.put("identifier", this.ownerId);

        timetableJson.put("data", timetableDataJson);
        return timetableJson;
    }

    public void save() {
        Log.i("Save", "Save process started");
        try {
            FileOutputStream fos = context.openFileOutput(saveFile, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, this.encoding);

            JSONObject timetable = this.toJson();
            Log.i("Save", timetable.toString(4));

            osw.write(timetable.toString());
            osw.close();

            Toast.makeText(this.context, "Erfolgreich gespeichert", Toast.LENGTH_LONG).show();
            Log.i("Save", "Save process finished");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadTimetable() {
        try {
            String timetableString = this.loadFromFile();
            if ( timetableString != "" ) {
                this.update(timetableString);
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

    public String loadFromFile() {
        FileInputStream fis;
        StringBuilder sb = new StringBuilder();
        try {
            fis = context.openFileInput(saveFile);
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

    public String getUniqueUserId() {
        return Settings.Secure.getString(this.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public void print() {
        try {
            String timetableString = this.loadFromFile();

            JSONArray timetable = new JSONArray(timetableString);
            for ( int i = 0; i < timetable.length(); i++ ) {
                JSONArray row = timetable.getJSONArray(i);
                Log.i("SAVE", row.toString());
            }
        } catch (JSONException  e) {
            e.printStackTrace();
        }
    }
}
