package com.dalydays.android.fullvolumetext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by edaly on 7/25/2016.
 */
public class MySMSReceiver extends BroadcastReceiver {

    private static final String TAG = MySMSReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";

        if (bundle != null) {
            // Retrieve the SMS messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // for each pdu
            for (int i = 0; i < msgs.length; i++) {
                // convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // sender's phone number
                str += "FullVolumeText received SMS from " + msgs[i].getOriginatingAddress() + " : ";
                // get the text message
                str += msgs[i].getMessageBody().toString() + "\n";
            }
        }

        // Disable do not disturb mode (if applied), and turn the volume all the way up
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_ALLOW_RINGER_MODES);

        // Show a toast message letting the user know if they happen to be watching the screen
        Toast.makeText(context, "FullVolumeText turned up your volume to the max!", Toast.LENGTH_LONG).show();
    }
}
