package com.dalydays.android.fullvolumetext;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by edaly on 7/25/2016.
 */
public class MySMSReceiver extends BroadcastReceiver {

    public static final String SMS_EXTRA_NAME = "pdus";
    private String TAG = MySMSReceiver.class.getSimpleName();

    public MySMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        SmsMessage[] smsMessages = null;

        String message = "";

        if ( extras != null ) {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

            for ( int i = 0; i < smsExtra.length; ++i ) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);

                String body = sms.getMessageBody().toString();
                String address = sms.getOriginatingAddress();

                message += "SMS from " + address + " :\n";
                message += body + "\n";
            }

            // Display SMS message
                Toast.makeText( context, message, Toast.LENGTH_SHORT ).show();
                Log.v(TAG, message);
            }
    }
}
