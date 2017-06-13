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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String LOG_TAG = MainActivity.class.getSimpleName();
    static final int MY_RECEIVE_SMS_REQUEST_CODE = 1;
    static final int PICK_CONTACT = 1;
    private static final int CONTACT_LOADER = 0;
    Button addContactButton;
    ListView mContactList;
    TextecutorCursorAdapter mCursorAdapter;

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
            // TODO Before jumping into the Do Not Disturb screen, we need to explain what's going on here
            // dialog window should explain "Textecutor needs control over Do Not Disturb settings in order to be able
            // to turn off Do Not Disturb mode. Please enable Do Not Disturb access for Textecutor on the next screen."
            showDoNotDisturbExplanation();
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

        mCursorAdapter = new TextecutorCursorAdapter(this, null);
        mContactList.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this);
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
            Log.e(LOG_TAG, "Failed to pick contact, resultCode: " + resultCode);
        }
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor;
        try {
            String phoneNo;
            String name;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            // get the phone number and parse it to remove non-numeric values
            phoneNo = cursor.getString(phoneIndex);
            phoneNo = phoneNo.replaceAll("[^\\d]", "");

            name = cursor.getString(nameIndex);
            Log.v(LOG_TAG, "Contact name/phone number: " + name + "/" + phoneNo);

            // If the contact already exists in the list, then don't add it again
            boolean alreadyAdded = false;
            String[] projection = {
                    AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER
            };
            Cursor existingLookup = getContentResolver().query(AllowedContactEntry.CONTENT_URI, projection, null, null, null);
            while (existingLookup.moveToNext()) {
                String number = existingLookup.getString(existingLookup.getColumnIndexOrThrow(AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER));
                if (phoneNo.equals(number)) {
                    alreadyAdded = true;
                    Toast.makeText(this, R.string.message_contact_already_added, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            existingLookup.close();

            if (!alreadyAdded) {
                /* Insert contact into allowed contact table */
                // create map of values
                ContentValues values = new ContentValues();
                values.put(AllowedContactEntry.COLUMN_NAME_NAME, name);
                values.put(AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER, phoneNo);

                // insert the new row using the content provider
                Uri newProductUri = getContentResolver().insert(AllowedContactEntry.CONTENT_URI, values);
                Long newRowId = ContentUris.parseId(newProductUri);

                if (newRowId >= 1) {
                    // I can't believe it's not failure!
                    Log.v(LOG_TAG, getString(R.string.message_add_allowed_contact_success));
                } else {
                    // DANG it failed
                    Log.e(LOG_TAG, getString(R.string.message_add_allowed_contact_fail));
                    Toast.makeText(this, R.string.message_add_allowed_contact_fail, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        // Check the results
        if (rowsDeleted > 0) {
            Log.v(LOG_TAG, getString(R.string.message_deletion_successful));
        }
        else {
            Toast.makeText(this, R.string.message_failed_to_delete_contact, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, getString(R.string.message_failed_to_delete_contact));
        }
    }

    private void showDoNotDisturbExplanation() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.do_not_disturb_question_title);
        builder.setMessage(R.string.do_not_disturb_explanation);
        builder.setPositiveButton(R.string.alert_proceed, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked go ahead button, so jump to do not disturb settings
                Intent getNotificationAccessIntent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(getNotificationAccessIntent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_RECEIVE_SMS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "Permission was granted for READ_SMS");
                }
                else {
                    // Permission denied, explain why this is wrong and bad
                    // We should show a dialog box with a quick explanation about why this is necessary for the app to function,
                    // and include a link to the app's permission settings so the user can manually enable it (in case we get
                    // to a point where the user denied, checked do not ask again, and will never see the runtime prompt again).
                    Log.d(LOG_TAG, "Permission was denied for READ_SMS");
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                AllowedContactEntry._ID,
                AllowedContactEntry.COLUMN_NAME_NAME,
                AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER
        };

        String sortOrder = AllowedContactEntry.COLUMN_NAME_NAME + " ASC";

        return new CursorLoader(this, AllowedContactEntry.CONTENT_URI, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}