package com.example.s0712338.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TableRow tr2 = (TableRow) findViewById(R.id.tr2);
        tr2.setBackgroundColor(Color.LTGRAY);
        TableRow tr4 = (TableRow) findViewById(R.id.tr4);
        tr4.setBackgroundColor(Color.LTGRAY);
    }
    public void changeClass(View view) {
        ;
    }
}
