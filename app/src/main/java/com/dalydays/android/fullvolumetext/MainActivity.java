package com.dalydays.android.fullvolumetext;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = MainActivity.class.getSimpleName();
    static final int MY_RECEIVE_SMS_REQUEST_CODE = 1;
    static final int PICK_CONTACT = 1;
    Button pickContactButton;
    TextView contactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // always ask for RECEIVE_SMS permission when opening the app, if not already set
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECEIVE_SMS}, MY_RECEIVE_SMS_REQUEST_CODE);
        }

        // always ask for Notification permission when opening the app, if not already set
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent getNotificationAccessIntent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(getNotificationAccessIntent);
        }

        pickContactButton = (Button) findViewById(R.id.btn_pick_contact);
        pickContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        contactInfo = (TextView) findViewById(R.id.tv_contact_info);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be using multiple startActivityForResult
            switch (requestCode) {
                case PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e(LOG_TAG, "Failed to pick contact");
        }
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            Log.d(LOG_TAG, "Contact name/phone number: " + name + "/" + phoneNo);
            // Set the value to the TextView
            contactInfo.setText(name + " / " + phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// TODO Allow the user to choose from a list of contacts from the contacts content provider, so that only the contacts chosen are allowed to send text commands
// TODO Allow the user to change the text string that initiates the full volume command
// TODO Allow the user to add different types of commands, with different text strings, and tied to different sets of authorized users (or a global list)
// TODO Wizard for implementing common use cases like full volume for trusted contacts
// TODO Help within the app explaining how to do things, as interactive as possible (or the app should be as intuitive as possible, so documentation is not required)
// TODO Implement a SQLite database to store preferences, authorized contacts, blocked numbers, etc.