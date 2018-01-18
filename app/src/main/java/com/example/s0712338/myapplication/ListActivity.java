package com.example.s0712338.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class ListActivity extends AppCompatActivity {

    HashMap<String, Integer> settingsHashMap = new HashMap<String, Integer>();
    public ArrayList<HashMap<String, String>> allTimetables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent i = getIntent();

        this.settingsHashMap = (HashMap<String, Integer>) i.getSerializableExtra("HashMap");
        this.allTimetables = (ArrayList<HashMap<String, String>>) i.getSerializableExtra("allTimetables");

        ArrayList<String> timetableUsernames = new ArrayList();
        for (HashMap<String, String> timetable : allTimetables) {
            timetableUsernames.add(timetable.get("username"));
        }

        ListView list = findViewById(R.id.timetableListView);
        ListAdapter foodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, timetableUsernames);
        list.setAdapter(foodAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = String.valueOf(parent.getItemAtPosition((int) id));
                Log.d("List", "Sending intent for timetable of " + value);
                startMainActivity(value);
            }
        });
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
                startMainActivity("");
                return true;

            case R.id.schedule_settings:
                this.startSettingsActivity();
                return true;

            case R.id.list:
                startListActivity();
                return true;

            // add other tools
        }
        return (super.onOptionsItemSelected(item));
    }

    /*load friends' timetables and view in main activity:
    1) share json file in whatsapp
    2) open json file with app -> timetable listed in All Timetables
    3) click any timetable in All Timetables -> view in MainActivity
    */

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

    private void startMainActivity(String username) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("HashMap", settingsHashMap);
        if ( !username.equals("") ) {
            i.putExtra("username", username);
        }
        this.startActivity(i);
    }
}