package com.dalydays.android.textecutor.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dalydays.android.textecutor.data.TextecutorContract.*;

/**
 * Created by edaly on 5/30/17.
 */

public class TextecutorDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "textecutor.db";
    private static final String CREATE_ALLOWED_CONTACT_TABLE = "CREATE TABLE " +
            AllowedContactEntry.TABLE_NAME + " (" + AllowedContactEntry._ID + " INTEGER PRIMARY KEY, " +
            AllowedContactEntry.COLUMN_NAME_NAME + " TEXT, " +
            AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER + " TEXT)";
    private static final String CREATE_ACTION_TABLE = "CREATE TABLE " + ActionEntry.TABLE_NAME + " (" + ActionEntry._ID + " INTEGER PRIMARY KEY, " +
            ActionEntry.COLUMN_NAME_ACTION + " TEXT, " +
            ActionEntry.COLUMN_NAME_DESCRIPTION + " TEXT, " +
            ActionEntry.COLUMN_NAME_ENABLED + " INTEGER)";

    public TextecutorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ALLOWED_CONTACT_TABLE);
        sqLiteDatabase.execSQL(CREATE_ACTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            sqLiteDatabase.execSQL(CREATE_ACTION_TABLE);
        }
        else {
            onCreate(sqLiteDatabase);
        }
    }
}
