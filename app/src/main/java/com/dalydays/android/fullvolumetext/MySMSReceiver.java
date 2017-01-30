package com.dalydays.android.fullvolumetext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by edaly on 7/25/2016.
 */
public class MySMSReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = MySMSReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String sender = "";
        String messageString = "";

        if (bundle != null) {
            // Retrieve the SMS messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // for each pdu
            for (int i = 0; i < msgs.length; i++) {
                // convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], (String) bundle.get("format"));

                // get sender's phone number
                if (i == 0) {
                    sender = msgs[i].getOriginatingAddress();
                }

                // get the text message
                messageString += msgs[i].getMessageBody().toString() + "\n";
            }
        }

        // Verify that the message matches the command needed to turn the volume up
        if (messageString.toLowerCase().contains("full volume")) {

            // Disable do not disturb mode (if applied), and turn the volume all the way up
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_ALLOW_RINGER_MODES);

            Toast.makeText(context, sender + " turned up your volume to the max!", Toast.LENGTH_LONG).show();
        }
        else {
            // debug toast
            Toast.makeText(context, sender + " text received, but didn't contain any commands.", Toast.LENGTH_LONG).show();
        }
    }
}
