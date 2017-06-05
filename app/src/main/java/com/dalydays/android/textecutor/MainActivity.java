package com.dalydays.android.textecutor;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dalydays.android.textecutor.data.TextecutorContract.*;
import com.dalydays.android.textecutor.data.TextecutorCursorAdapter;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = MainActivity.class.getSimpleName();
    static final int MY_RECEIVE_SMS_REQUEST_CODE = 1;
    static final int PICK_CONTACT = 1;
    Button addContactButton;
    ListView mContactList;

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

        addContactButton = (Button) findViewById(R.id.btn_add_authorized_contact);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        // set up ListView and adapter
        mContactList = (ListView) findViewById(R.id.allowed_contacts_list);
        mContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showDeleteConfirmationDialog(id);
            }
        });
        printAllowedContactList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be using multiple startActivityForResult
            switch (requestCode) {
                case PICK_CONTACT:
                    contactPicked(data);
                    printAllowedContactList();
                    break;
            }
        } else {
            Log.e(LOG_TAG, "Failed to pick contact, resultCode: " + resultCode);
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

            /* Insert contact into allowed contact table */
            // create map of values
            ContentValues values = new ContentValues();
            values.put(AllowedContactEntry.COLUMN_NAME_NAME, name);
            values.put(AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER, phoneNo);

            // insert the new row using the content provider
            Uri newProductUri = getContentResolver().insert(AllowedContactEntry.CONTENT_URI, values);
            Long newRowId = ContentUris.parseId(newProductUri);

            if (newRowId >= 1) {
                // OMG it actually worked!
                Toast.makeText(this, "OMG You added a contact to the allowed list!", Toast.LENGTH_SHORT).show();
            }
            else {
                // DANG fail
                Toast.makeText(this, "DANG it failed!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printAllowedContactList() {
        String[] projection = {
                AllowedContactEntry._ID,
                AllowedContactEntry.COLUMN_NAME_NAME,
                AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER
        };

        String sortOrder = AllowedContactEntry.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = getContentResolver().query(AllowedContactEntry.CONTENT_URI, projection, null, null, sortOrder);

        TextecutorCursorAdapter contactCursorAdapter = new TextecutorCursorAdapter(this, cursor);
        mContactList.setAdapter(contactCursorAdapter);
    }

    private void showDeleteConfirmationDialog(final long contactId) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirmation_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the contact.
                deleteContact(contactId);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteContact(long contactId) {
        // Get the contact Uri
        Uri contactContentUri = ContentUris.withAppendedId(AllowedContactEntry.CONTENT_URI, contactId);

        int rowsDeleted = getContentResolver().delete(contactContentUri, null, null);

        printAllowedContactList();

        // Show a toast with the results
        if (rowsDeleted > 0) {
            Toast.makeText(this, R.string.toast_message_deletion_successful, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, R.string.toast_message_failed_to_delete_contact, Toast.LENGTH_SHORT).show();
        }
    }
}

// DONE Implement a SQLite database to store preferences, authorized contacts, blocked numbers, etc.
// DONE Allow the user to choose from a list of contacts from the contacts content provider, so that only the contacts chosen are allowed to send text commands
// TODO Next, implement CursorLoader and use notify technique instead of always calling printAllowedContactList()
// TODO Allow the user to change the text string that initiates the full volume command
// TODO Allow the user to add different types of commands, with different text strings, and tied to different sets of authorized users (or a global list)
// TODO Wizard for implementing common use cases like full volume for trusted contacts
// TODO Help within the app explaining how to do things, as interactive as possible (or the app should be as intuitive as possible, so documentation is not required)
// TODO Add empty view when list is empty