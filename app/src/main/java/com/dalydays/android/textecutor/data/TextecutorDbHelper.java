package com.dalydays.android.textecutor.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dalydays.android.textecutor.R;
import com.dalydays.android.textecutor.data.TextecutorContract.*;

/**
 * Created by edaly on 5/30/17.
 */

public class TextecutorDbHelper extends SQLiteOpenHelper {

    private static String LOG_TAG = TextecutorDbHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "textecutor.db";

    /**
     * Create tables
     */
    private static final String CREATE_ALLOWED_CONTACT_TABLE = "CREATE TABLE IF NOT EXISTS " +
            AllowedContactEntry.TABLE_NAME + " (" +
            AllowedContactEntry._ID + " INTEGER PRIMARY KEY, " +
            AllowedContactEntry.COLUMN_NAME_NAME + " TEXT, " +
            AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER + " TEXT" +
            ")";
    private static final String CREATE_ACTION_TABLE = "CREATE TABLE IF NOT EXISTS " +
            ActionEntry.TABLE_NAME + " (" +
            ActionEntry._ID + " INTEGER PRIMARY KEY, " +
            ActionEntry.COLUMN_NAME_ACTION + " TEXT, " +
            ActionEntry.COLUMN_NAME_DESCRIPTION + " TEXT, " +
            ActionEntry.COLUMN_NAME_ENABLED + " INTEGER" +
            ")";
    private static final String CREATE_ACTION_CONTACT_TABLE = "CREATE TABLE IF NOT EXISTS " +
            ActionContactEntry.TABLE_NAME + " (" +
            ActionContactEntry._ID + " INTEGER PRIMARY KEY, " +
            ActionContactEntry.COLUMN_NAME_ACTION + " INTEGER, " +
            ActionContactEntry.COLUMN_NAME_CONTACT + " INTEGER, " +
            "FOREIGN KEY(" + ActionContactEntry.COLUMN_NAME_ACTION + ") REFERENCES " + ActionEntry.TABLE_NAME + "(" + ActionEntry._ID + "), " +
            "FOREIGN KEY(" + ActionContactEntry.COLUMN_NAME_CONTACT + ") REFERENCES " + AllowedContactEntry.TABLE_NAME + "(" + AllowedContactEntry._ID + ")" +
            ")";

    /**
     * Initialize Data
     */
    private static String INSERT_ACTION_FULL_VOLUME;


    public TextecutorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        INSERT_ACTION_FULL_VOLUME = "INSERT INTO " +
                ActionEntry.TABLE_NAME + " (" +
                ActionEntry.COLUMN_NAME_ACTION + ", " +
                ActionEntry.COLUMN_NAME_DESCRIPTION + ", " +
                ActionEntry.COLUMN_NAME_ENABLED + ") VALUES (" +
                "\"" + context.getString(R.string.action_full_volume_name) + "\", " +
                "\"" + context.getString(R.string.action_full_volume_description) + "\"" +
                ", 0)";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "Inserting allowed contact table: " + CREATE_ALLOWED_CONTACT_TABLE);
        sqLiteDatabase.execSQL(CREATE_ALLOWED_CONTACT_TABLE);

        Log.d(LOG_TAG, "Inserting action table: " + CREATE_ACTION_TABLE);
        sqLiteDatabase.execSQL(CREATE_ACTION_TABLE);

        Log.d(LOG_TAG, "Inserting action_contact table: " + CREATE_ACTION_CONTACT_TABLE);
        sqLiteDatabase.execSQL(CREATE_ACTION_CONTACT_TABLE);

        Log.d(LOG_TAG, "Inserting action: " + INSERT_ACTION_FULL_VOLUME);
        sqLiteDatabase.execSQL(INSERT_ACTION_FULL_VOLUME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 2:
                sqLiteDatabase.execSQL(CREATE_ACTION_TABLE);
                sqLiteDatabase.execSQL(INSERT_ACTION_FULL_VOLUME);
            case 3:
                sqLiteDatabase.execSQL(CREATE_ACTION_CONTACT_TABLE);
        }
    }
}
