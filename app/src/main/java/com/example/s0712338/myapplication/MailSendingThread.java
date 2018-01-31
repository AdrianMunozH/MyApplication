package com.example.s0712338.myapplication;

import android.content.Context;
import android.widget.Toast;


public class MailSendingThread implements Runnable {

    Context context;
    String file;

    public MailSendingThread(Context context, String file) {
        this.context = context;
        this.file = file;
    }

    public String getAbsoluteFilePath() {
        return context.getFilesDir().getAbsolutePath() + "/" + this.file;
    }

    public void run() {
        try {
            MailSender sender = new MailSender();
            sender.addAttachment(this.getAbsoluteFilePath());
            sender.sendMail("Timetable update", "",
                    "tudresdenstundenplan@gmail.com",
                    "tudresdenstundenplan@gmail.com");
        } catch (Exception e) {
            Toast.makeText(context,"Konnte leider nicht synchronisiseren",Toast.LENGTH_LONG).show();
        }
    }
}
