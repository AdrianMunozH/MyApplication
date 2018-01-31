package com.example.s0712338.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText usernameTextEdit;
    private EditText first;
    private EditText last;
    private EditText cLength;
    private EditText bLength;
    private TimePicker mTimePicker;
    private Switch syncSwitch;
    private Button btnSet;
    private TextView registerCommand;

    HashMap<String, Integer> settingsHashMap = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent i = getIntent();

        settingsHashMap = (HashMap<String, Integer>) i.getSerializableExtra("HashMap");

        usernameTextEdit = (EditText) findViewById(R.id.username);
        first = (EditText) findViewById(R.id.firstClass);
        last = (EditText) findViewById(R.id.lastClass);
        cLength = (EditText) findViewById(R.id.classLength);
        bLength = (EditText) findViewById(R.id.breakLength);
        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);
        syncSwitch = (Switch) findViewById(R.id.telegramSyncSwitch);
        registerCommand = (TextView) findViewById(R.id.syncRegisterCommand);

        usernameTextEdit.setText(i.getStringExtra("username"));
        first.setText(settingsHashMap.get("firstClass").toString());
        last.setText(settingsHashMap.get("lastClass").toString());
        cLength.setText(settingsHashMap.get("length").toString());
        bLength.setText(settingsHashMap.get("break").toString());
        mTimePicker.setCurrentHour(settingsHashMap.get("startHour"));
        mTimePicker.setCurrentMinute(settingsHashMap.get("startMin"));
        if (settingsHashMap.get("sync") > 0) {
            syncSwitch.setChecked(true);
            showRegisterCommand(true);
        } else {
            syncSwitch.setChecked(false);
            showRegisterCommand(false);
        }

        syncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("HardwareIds")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showRegisterCommand(isChecked);
            }
        });

        InitializeActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.my_schedule:
                startMainActivity();
                return true;

            case R.id.schedule_settings:
                startSettingsActivity();
                return true;

            case R.id.list:
                this.startListActivity();
                return true;
            // add other tools
        }
        return(super.onOptionsItemSelected(item));
    }

    private void InitializeActivity() {
        btnSet = (Button) findViewById(R.id.btnSettings);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applySettings();
            }
        });
    }

    private void applySettings(){
        String new_username = usernameTextEdit.getText().toString();

        settingsHashMap.put("firstClass",Integer.parseInt(first.getText().toString()));
        settingsHashMap.put("lastClass",Integer.parseInt(last.getText().toString()));
        settingsHashMap.put("length",Integer.parseInt(cLength.getText().toString()));
        settingsHashMap.put("break",Integer.parseInt(bLength.getText().toString()));
        settingsHashMap.put("startHour",mTimePicker.getCurrentHour());
        settingsHashMap.put("startMin",mTimePicker.getCurrentMinute());

        if (syncSwitch.isChecked()) {
            settingsHashMap.put("sync", 1);
        } else {
            settingsHashMap.put("sync", 0);
        }

        Intent saveIntent = new Intent(this, MainActivity.class);
        saveIntent.putExtra("HashMap", settingsHashMap);
        saveIntent.putExtra("username", new_username);
        this.startActivity(saveIntent);
    }

    @SuppressLint("HardwareIds")
    private void showRegisterCommand(Boolean show) {
        if (show) {
            String id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String CommandString = "/register " + id;
            registerCommand.setText(CommandString);
        } else {
            registerCommand.setText("");
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
