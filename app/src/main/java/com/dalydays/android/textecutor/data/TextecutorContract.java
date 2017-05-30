package com.dalydays.android.textecutor.data;

import android.provider.BaseColumns;

/**
 * Created by edaly on 5/30/17.
 */

public final class TextecutorContract {
    // private constructor prevents accidental instantiation of the contract class
    private TextecutorContract() {}

    /**
     * AllowedContacts keeps track of contacts that are allowed to send commands in text messages
     */
    public static class AllowedContacts implements BaseColumns {
        
    }
}
