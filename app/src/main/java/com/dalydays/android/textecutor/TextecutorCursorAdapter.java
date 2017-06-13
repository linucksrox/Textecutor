package com.dalydays.android.textecutor;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.dalydays.android.textecutor.R;
import com.dalydays.android.textecutor.data.TextecutorContract.*;

/**
 * Created by edaly on 5/31/2017.
 */

public class TextecutorCursorAdapter extends CursorAdapter {

    public TextecutorCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView contactNameTV = (TextView) view.findViewById(R.id.tv_contact_name);
        TextView contactNumberTV = (TextView) view.findViewById(R.id.tv_contact_number);

        String contactNameString = cursor.getString(cursor.getColumnIndexOrThrow(AllowedContactEntry.COLUMN_NAME_NAME));
        String contactNumberString = cursor.getString(cursor.getColumnIndexOrThrow(AllowedContactEntry.COLUMN_NAME_PHONE_NUMBER));

        contactNameTV.setText(contactNameString);
        contactNumberTV.setText(contactNumberString);
    }
}
