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

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private TimetableManager timetableManager = null;
    private Timetable timetable = null;

    public String saveFile = "timetable.json";

    public String username = "Nick Lehmann"; // TODO: User should be able to edit this
    HashMap<String, Integer> settingsHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("DEBUG", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent saveIntent = getIntent();

        if (saveIntent.getExtras() == null) {
            settingsHashMap.put("firstClass", 1);
            settingsHashMap.put("lastClass", 5);
            settingsHashMap.put("length", 90);
            settingsHashMap.put("break", 20);
            settingsHashMap.put("startHour", 7);
            settingsHashMap.put("startMin", 30);
        } else {
            settingsHashMap = (HashMap<String, Integer>) saveIntent.getSerializableExtra("HashMap");
        }

        this.timetableManager = new TimetableManager(this,
                (TableLayout) findViewById(R.id.tableLayout), saveFile, settingsHashMap, username);

        if ( saveIntent.hasExtra("username") ) {
            this.timetable = timetableManager.getTimetable(saveIntent.getStringExtra("username"));
        } else {
            this.timetable = timetableManager.getMyTimetable();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
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
                this.timetableManager.save();
                return true;

            // add share function - click button, share josn file in whatsapp
            case R.id.share:
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void startListActivity() {
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra("HashMap", settingsHashMap);
        i.putExtra("allTimetables", this.timetableManager.allTimetables);
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