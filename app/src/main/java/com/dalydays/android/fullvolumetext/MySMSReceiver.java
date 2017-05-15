package com.dalydays.android.fullvolumetext;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

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

        // Verify that the sender is allowed and the message matches the command needed to turn the volume up
//        if (sender.contains("6505551212")) {
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
            }
            else {
                // debug toast
                Toast.makeText(context, sender + " text received from authorized user, but didn't contain any commands.", Toast.LENGTH_LONG).show();
            }
        }
//        else {
//            Toast.makeText(context, sender + " text received, but not allowed to send commands.", Toast.LENGTH_LONG).show();
//        }
//    }
}
