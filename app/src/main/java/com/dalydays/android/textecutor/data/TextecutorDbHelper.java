package com.dalydays.android.textecutor.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dalydays.android.textecutor.data.TextecutorContract.*;

/**
 * Created by edaly on 5/30/17.
 */

public class TextecutorDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "textecutor.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            AllowedContactEntry.TABLE_NAME + " (" + AllowedContactEntry._ID + " INTEGER PRIMARY KEY, " +
            AllowedContactEntry.COLUMN_NAME_NAME + " TEXT, " +
            AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER + " TEXT);";

    public TextecutorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
