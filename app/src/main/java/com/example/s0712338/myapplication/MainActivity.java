package com.example.s0712338.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public TableLayout tableLayout;

    public String[] lessonTimes = new String[10];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        initLessonTimes();

        buildTimetableLayout();
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

            tableLayout.addView(newRow);

            TableLayout.LayoutParams params = (TableLayout.LayoutParams) newRow.getLayoutParams();
            params.leftMargin = 30;
            params.rightMargin = 30;
            newRow.setLayoutParams(params);

            if ( i % 2 == 0 ) {
                newRow.setBackgroundColor(Color.LTGRAY);
            }
        }
    }
}
