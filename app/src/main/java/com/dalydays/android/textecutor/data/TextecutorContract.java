package com.dalydays.android.textecutor.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by edaly on 5/30/17.
 */

public final class TextecutorContract {

    public static final String CONTENT_AUTHORITY = "com.dalydays.android.textecutor";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ALLOWED_CONTACT = "allowedcontact";

    // private constructor prevents accidental instantiation of the contract class
    private TextecutorContract() {}

    /**
     * AllowedContacts keeps track of contacts that are allowed to send commands in text messages
     */
    public static class AllowedContactEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_ALLOWED_CONTACT);
        public static final String PRODUCT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALLOWED_CONTACT;
        public static final String PRODUCT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALLOWED_CONTACT;
        public static final String TABLE_NAME = "allowedcontact";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phonenumber";
    }
}
