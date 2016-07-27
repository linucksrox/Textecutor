package com.dalydays.android.fullvolumetext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by edaly on 7/25/2016.
 */
public class MySMSReceiver extends BroadcastReceiver {

    private static final String TAG = MySMSReceiver.class.getSimpleName();

    public MySMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";

        if (bundle != null) {
            // Retrieve the SMS messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // for every SMS message received
            for (int i = 0; i < msgs.length; i++) {
                // convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // sender's phone number
                str += "FullVolumeText received SMS from " + msgs[i].getOriginatingAddress() + " : ";
                // get the text message
                str += msgs[i].getMessageBody().toString() + "\n";
            }

            // Display SMS message
                Log.d(TAG, str);
            }
    }
}
