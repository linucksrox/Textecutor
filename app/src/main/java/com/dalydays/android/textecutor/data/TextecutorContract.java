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
    public static final String PATH_ACTION = "action";
    public static final String PATH_ACTION_CONTACT = "action_contact";

    // private constructor prevents accidental instantiation of the contract class
    private TextecutorContract() {}

    /**
     * The allowedcontact table keeps track of contacts that are allowed to send commands in text messages
     */
    public static class AllowedContactEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_ALLOWED_CONTACT);
        public static final String CONTACT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALLOWED_CONTACT;
        public static final String CONTACT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALLOWED_CONTACT;
        public static final String TABLE_NAME = "allowedcontact";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phonenumber";
    }

    /**
     * The action table keeps track of available actions and whether they are enabled or disabled
     */
    public static class ActionEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_ACTION);
        public static final String TABLE_NAME = "action";
        public static final String COLUMN_NAME_ACTION = "action";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_ENABLED = "enabled";
    }

    /**
     * The action_contact table is a tie table linking allowed contacts to available actions
     */
    public static class ActionContactEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_ACTION_CONTACT);
        public static final String TABLE_NAME = "action_contact";
        public static final String COLUMN_NAME_ACTION = "action";
        public static final String COLUMN_NAME_CONTACT = "contact";
    }
}
