package com.dalydays.android.textecutor;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.dalydays.android.textecutor.data.TextecutorContract;

/**
 * Created by edaly on 7/25/2016.
 */
public class MySMSReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = MySMSReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        String sender = "";
        String messageString = "";

        if (bundle != null) {
            // Retrieve the SMS messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] msgs = new SmsMessage[pdus.length];

            // for each pdu
            for (int i = 0; i < msgs.length; i++) {
                // convert Object array
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                }
                else {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                // get sender's phone number
                if (i == 0) {
                    sender = msgs[i].getOriginatingAddress();
                }

                // get the text message
                messageString += msgs[i].getMessageBody() + "\n";
            }
        }

        /* Verify that the sender is allowed and the message matches the command needed to turn the volume up */
        // Get a list of all numbers on the allowed list
        String[] projection = {
                TextecutorContract.AllowedContactEntry._ID,
                TextecutorContract.AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER
        };

        Cursor cursor = context.getContentResolver().query(TextecutorContract.AllowedContactEntry.CONTENT_URI, projection, null, null, null);

        String number;

        while (cursor.moveToNext()) {
            number = cursor.getString(cursor.getColumnIndexOrThrow(TextecutorContract.AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER));
            if (sender.contains(number)) {
                if (messageString.toLowerCase().contains("full volume")) {

                    // check if we have permission to change notification settings, otherwise quit
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
                        // this should be a notification eventually, but for now a toast will have to do
                        Toast.makeText(context, "You need to give this app permission to change notification settings!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Disable do not disturb mode (if applied), and turn the volume all the way up
                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    // set ringer mode to normal, taking device out of silent mode if applicable
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    // turn up volume to the max
                    am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_ALLOW_RINGER_MODES);

                    Toast.makeText(context, sender + " turned up your volume to the max!", Toast.LENGTH_LONG).show();
                    Log.v(LOG_TAG, sender + " turned your volume to the max!");
                }
                else {
                    // debug toast
                    Log.v(LOG_TAG, " text received from authorized user, but didn't contain any commands.");
                }
            }
            else {
                Log.v(LOG_TAG, " text received, but sender " + sender + " does not match authorized user " + number);
            }
        }
    }
}
