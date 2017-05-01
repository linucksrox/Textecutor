package com.dalydays.android.fullvolumetext;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static final int MY_RECEIVE_SMS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // always ask for RECEIVE_SMS permission when opening the app, if not already set
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECEIVE_SMS}, MY_RECEIVE_SMS_REQUEST_CODE);
        }


    }
}

// TODO Allow the user to choose from a list of contacts from the contacts content provider, so that only the contacts chosen are allowed to send text commands
// TODO Allow the user to change the text string that initiates the full volume command
// TODO Allow the user to add different types of commands, with different text strings, and tied to different sets of authorized users (or a global list)
// TODO Wizard for implementing common use cases like full volume for trusted contacts
// TODO Help within the app explaining how to do things, as interactive as possible (or the app should be as intuitive as possible, so documentation is not required)
// TODO Implement a SQLite database to store preferences, authorized contacts, blocked numbers, etc.