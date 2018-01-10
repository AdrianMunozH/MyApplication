package com.example.s0712338.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Timetable {

    public TableLayout timetableLayout;
    List<TableRow> rows;
    public String[] lessonTimes = new String[10];
    public String saveFile;
    public String encoding;
    public Context context;
    public int timetableRowCount;
    public int backgroundColor;
    public String[] day ={"Montag","Dienstag", "Mittwoch", "Donnerstag", "Freitag"};
    public Timetable(Context context, TableLayout timetableLayout, String saveFile) {
        this.context = context;
        this.timetableLayout = timetableLayout;
        this.saveFile = saveFile;
        this.rows = new ArrayList<TableRow>();

        this.encoding = "UTF-8";
        this.timetableRowCount = 5;
        this.backgroundColor = Color.LTGRAY;
    }


    public void buildTimetableLayout(int tC,String[] lessonTimes2) {
        for ( int i = 1; i <= (tC); i++ ) {
            TableRow newRow = new TableRow(context);

            // Add lesson time to row
            TextView newTextView = new TextView(context);
            newTextView.setPadding(20, 5, 20, 5);
            if(i>1)newTextView.setText(lessonTimes2[i-1]);
            newRow.addView(newTextView);
            // Add edit text widgets for lessons to row
            for (int j = 1; j <= 5; j++) {
                if (i == 1) {
                    TextView newTextView2 = new TextView(context);
                    newTextView2.setPadding(20, 5, 20, 5);
                    newTextView2.setText(day[j-1]);
                    newRow.addView(newTextView2);
                } else {
                    EditText newTextEdit = new EditText(context);
                    newRow.addView(newTextEdit);
                    newTextEdit.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    newTextEdit.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
            timetableLayout.addView(newRow);
            rows.add(newRow);

            // Set layout params of row
            TableLayout.LayoutParams params = (TableLayout.LayoutParams) newRow.getLayoutParams();
            params.leftMargin = 30;
            params.rightMargin = 30;
            newRow.setLayoutParams(params);

            // set background colour for every second row
            if ( i % 2 == 0 ) {
                newRow.setBackgroundColor(this.backgroundColor);
            }
        }
    }


    public JSONArray toJson() {
        JSONArray timetable = new JSONArray();

        for (int i = 0; i < timetableLayout.getChildCount(); i++) {
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


    public void save() {
        Log.i("Save", "Save process started");
        try {
            FileOutputStream fos = context.openFileOutput(saveFile, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, this.encoding);

            JSONArray timetable = this.toJson();
            Log.i("Save", timetable.toString(4));

            osw.write(timetable.toString());
            osw.close();
            Toast.makeText(this.context, "Erfolgreich gespeichert", Toast.LENGTH_LONG).show();
            Log.i("Save", "Save process finished");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void loadTimetable() {
        String timetableString = this.loadFromFile();
        if ( timetableString != "" ) {
            this.update(timetableString);
        }
    }


    public void update(String timetableString) {
        try {
            JSONArray timetableJSON = new JSONArray(timetableString);

            for ( int i = 0; i < timetableJSON.length(); i++ ) {
                JSONArray timetableRowJson = timetableJSON.getJSONArray(i);
                TableRow timetableRow = rows.get(i);

                for ( int j = 1; j < timetableRow.getChildCount(); j++ ) {
                    String lesson = timetableRowJson.getString(j-1);
                    EditText cell = (EditText) timetableRow.getChildAt(j);
                    cell.setText(lesson);
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
