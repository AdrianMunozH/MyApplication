package com.example.s0712338.myapplication;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public abstract class TimetableBase {
    public String encoding = "UTF-8";
    public String file;
    public Context context;

    // Provides functions for writing to and reading
    // from files; only work with strings
    // ----------------------------------------------
    public String loadFromFile() {
        StringBuilder sb = new StringBuilder();
        try {
            Log.d("DEBUG", this.context.getPackageName());
            Log.d("DEBUG", this.file);
            FileInputStream fis = context.openFileInput(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, this.encoding));
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return sb.toString();
    }

    public void writeToFile(String content) {
        try {
            FileOutputStream fos = context.openFileOutput(this.file, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, this.encoding);

            Log.i("Save", content);

            osw.write(content);
            osw.close();

            Toast.makeText(this.context, "Erfolgreich gespeichert", Toast.LENGTH_LONG).show();
            Log.i("Save", "Save process finished");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loading, saving and conversion has (!) to
    // be implemented by subclasses
    // --------------------------------------------
    public abstract Object toJson();
    public abstract void load();
    public abstract void save();
}
