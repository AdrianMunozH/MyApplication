package com.example.s0712338.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private Timetable timetable = null;

    public String[] lessonTimes = new String[10];
    public Integer[] lessonHours = new Integer[10];
    public Integer[] lessonMins = new Integer[10];

    public String saveFile = "timetable.json";

    HashMap<String, Integer> settingsHashMap = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("DEBUG", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timetable = new Timetable(this, (TableLayout) findViewById(R.id.tableLayout), this.saveFile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {

            settingsHashMap.put("firstClass", 1);
            settingsHashMap.put("lastClass", 5);
            settingsHashMap.put("length", 90);
            settingsHashMap.put("break", 20);
            settingsHashMap.put("startHour", 9);
            settingsHashMap.put("startMin", 20);
        } else settingsHashMap = (HashMap<String, Integer>) saveIntent.getSerializableExtra("HashMap");


        //initButtons();

        initLessonTimes();
        if(settingsHashMap.get("lastClass") != 0){
            timetable.buildTimetableLayout(settingsHashMap.get("lastClass"), lessonTimes);
        } else {
            timetable.buildTimetableLayout(3, lessonTimes);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("Debug", "onOptionsItemSelected");
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
                this.timetable.save();
                return true;

            // add share function - click button, share josn file in whatsapp
            case R.id.share:
                this.timetable.save();
                return true;


        }
        return (super.onOptionsItemSelected(item));
    }

    /* delete buttons (move to appbar), menuButton - was ist Printtimetable ???
    public void initButtons() {
        Log.i("Debug", "initButtons");
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timetable.save();
            }
        });

        findViewById(R.id.menuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timetable.print();
            }
        });
    } */

    public void initLessonTimes() {

        Log.i("Debug", "in initlessongtimes");

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
    public void startLessonActivity( View view) {
        Intent intent = new Intent(this, LessonActivity.class);
        intent.putExtra("lesson", "text");
        this.startActivity(intent);
    }

    public void initTV(TextView textViewLesson) {
        textViewLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLessonActivity(view);
            }
        });
    }
}