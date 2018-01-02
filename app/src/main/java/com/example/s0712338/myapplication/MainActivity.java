package com.example.s0712338.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    public TableLayout timetableLayout;

    public String[] lessonTimes = new String[10];
    public Integer[] lessonHours = new Integer[10];
    public Integer[] lessonMins = new Integer[10];

    public String saveFile = "timetable.json";


    HashMap<String, Integer> settingsHashMap = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timetableLayout = findViewById(R.id.tableLayout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {

            settingsHashMap.put("firstClass", 1);
            settingsHashMap.put("lastClass", 2);
            settingsHashMap.put("length", 90);
            settingsHashMap.put("break", 20);
            settingsHashMap.put("startHour", 9);
            settingsHashMap.put("startMin", 20);
        }

        if (saveIntent.getExtras() != null) {
            settingsHashMap = (HashMap<String, Integer>) saveIntent.getSerializableExtra("HashMap");

        }

        initButtons();

        initLessonTimes();

        buildTimetableLayout();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.my_schedule:
                startMainActivity();
                return true;

            case R.id.list:
                this.startListActivity();
                return true;

            case R.id.schedule_settings:
                this.startSettingsActivity();
                return true;

            // save function here instead of button
            case R.id.save:
                this.saveTimetable();
                return true;

            // add share function - click button, share josn file in whatsapp
            case R.id.share:
                this.saveTimetable();
                return true;


        }
        return (super.onOptionsItemSelected(item));
    }

    // delete buttons (move to appbar), menuButton - was ist Printtimetable ???
    public void initButtons() {
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTimetable();
            }
        });

        findViewById(R.id.menuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printTimetable();
            }
        });
    }

    public void initLessonTimes() {

        lessonHours[0] = settingsHashMap.get("startHour");
        lessonMins[0] = settingsHashMap.get("startMin");
        lessonTimes[0] = lessonHours[0].toString() + ":" + lessonMins[0].toString();

        for (int i = 1; i < settingsHashMap.get("lastClass")-1; i++)
        {
            Integer hDif = ((settingsHashMap.get("length") + settingsHashMap.get("break"))) / 60;
            Integer mDif = ((settingsHashMap.get("length") + settingsHashMap.get("break"))) % 60;
            hDif = (lessonMins[i-1] + mDif >= 60) ? hDif + 1 : hDif;
            mDif = (lessonMins[i-1] + mDif >= 60) ? mDif - 60 : mDif;
            lessonHours[i]=lessonHours[i-1]+hDif;
            lessonMins[i]=lessonMins[i-1]+mDif;

            lessonTimes[i] = lessonHours[i].toString() + ":" + lessonMins[i].toString();
        }
    }


    public void buildTimetableLayout() {
        for (int i = 1; i <= settingsHashMap.get("lastClass"); i++) {
            TableRow newRow = new TableRow(this);

            TextView newTextView = new TextView(this);
            newTextView.setText(lessonTimes[i - 1]);
            newRow.addView(newTextView);

            for (int j = 1; j <= settingsHashMap.get("lastClass"); j++) {
                EditText newEditText = new EditText(this);
                newRow.addView(newEditText);

                newEditText.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                newEditText.setGravity(Gravity.CENTER_HORIZONTAL);
            }

            timetableLayout.addView(newRow);

            TableLayout.LayoutParams params = (TableLayout.LayoutParams) newRow.getLayoutParams();
            params.leftMargin = 30;
            params.rightMargin = 30;
            newRow.setLayoutParams(params);

            if (i % 2 == 0) {
                newRow.setBackgroundColor(Color.LTGRAY);
            }
        }
    }

    public JSONArray timetableToJson() {
        JSONArray timetable = new JSONArray();

        for (int i = 0; i < timetableLayout.getChildCount(); i++) {
            View child = timetableLayout.getChildAt(i);
            JSONArray timetableRow = new JSONArray();

            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;

                for (int j = 1; j < row.getChildCount(); j++) {
                    EditText cell = (EditText) row.getChildAt(j);
                    timetableRow.put(cell.getText().toString());
                }
            }
            timetable.put(timetableRow);
        }
        return timetable;
    }


    public void saveTimetable() {
        Log.i("Save", "Save process started");
        try {
            FileOutputStream fos = openFileOutput(saveFile, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");

            JSONArray timetable = timetableToJson();

            Log.i("SAVE", timetable.toString(4));
            osw.write(timetable.toString());
            osw.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadTimetableFromFile() {
        FileInputStream fis;
        StringBuilder sb = new StringBuilder();
        try {
            fis = openFileInput(saveFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void printTimetable() {
        try {
            String timetableString = loadTimetableFromFile();

            JSONArray timetable = new JSONArray(timetableString);
            for (int i = 0; i < timetable.length(); i++) {
                JSONArray row = timetable.getJSONArray(i);
                Log.i("SAVE", row.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void startListActivity() {
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra("HashMap", settingsHashMap);
        this.startActivity(i);
    }

    private void startSettingsActivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("HashMap", settingsHashMap);
        this.startActivity(i);
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("HashMap", settingsHashMap);
        this.startActivity(i);
    }
}