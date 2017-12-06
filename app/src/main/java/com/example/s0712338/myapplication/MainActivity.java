package com.example.s0712338.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
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


public class MainActivity extends AppCompatActivity {

    public TableLayout timetableLayout;

    public String[] lessonTimes = new String[10];

    public String saveFile = "timetable.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timetableLayout = findViewById(R.id.tableLayout);

        initButtons();

        initLessonTimes();

        buildTimetableLayout();
    }


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
        lessonTimes[0] = "7:30";
        lessonTimes[1] = "9:20";
        lessonTimes[2] = "11:10";
        lessonTimes[3] = "13:00";
        lessonTimes[4] = "14:50";
    }


    public void buildTimetableLayout() {
        for ( int i = 1; i <= 5; i++ ) {
            TableRow newRow = new TableRow(this);

            TextView newTextView = new TextView(this);
            newTextView.setText(lessonTimes[i - 1]);
            newRow.addView(newTextView);

            for (int j = 1; j <= 5; j++) {
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

            if ( i % 2 == 0 ) {
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
        } catch (IOException|JSONException e) {
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
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
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
            for ( int i = 0; i < timetable.length(); i++ ) {
                JSONArray row = timetable.getJSONArray(i);
                Log.i("SAVE", row.toString());
            }
        } catch (JSONException  e) {
            e.printStackTrace();
        }

    }
}
