package com.dalydays.android.textecutor.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.dalydays.android.textecutor.data.TextecutorContract.*;

/**
 * Created by edaly on 5/31/17.
 */

public class TextecutorProvider extends ContentProvider {
    public static final String LOG_TAG = TextecutorProvider.class.getSimpleName();
    private TextecutorDbHelper mDbHelper;

    // Set up URI matcher codes
    private static final int CONTACTS = 100;
    private static final int CONTACT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(TextecutorContract.CONTENT_AUTHORITY, TextecutorContract.PATH_ALLOWED_CONTACT, CONTACTS);
        sUriMatcher.addURI(TextecutorContract.CONTENT_AUTHORITY, TextecutorContract.PATH_ALLOWED_CONTACT + "/#", CONTACT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new TextecutorDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                cursor = database.query(AllowedContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CONTACT_ID:
                selection = AllowedContactEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(AllowedContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return AllowedContactEntry.CONTACT_LIST_TYPE;
            case CONTACT_ID:
                return AllowedContactEntry.CONTACT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return insertContact(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertContact(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(AllowedContactEntry.TABLE_NAME, null, values);

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] whereArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int numRowsDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                // Delete all rows that match the whereClause and whereArgs
                numRowsDeleted = database.delete(AllowedContactEntry.TABLE_NAME, whereClause, whereArgs);
                break;
            case CONTACT_ID:
                // Delete a single row from the pets table using the given ID
                whereClause = AllowedContactEntry._ID + "=?";
                whereArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                numRowsDeleted = database.delete(AllowedContactEntry.TABLE_NAME, whereClause, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}