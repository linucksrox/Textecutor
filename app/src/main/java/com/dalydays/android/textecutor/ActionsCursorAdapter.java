package com.dalydays.android.textecutor;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.dalydays.android.textecutor.data.TextecutorContract.ActionEntry;

/**
 * Created by edaly on 10/17/2017.
 */

public class ActionsCursorAdapter extends CursorAdapter {

    public ActionsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.action_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView contactNameTV = (TextView) view.findViewById(R.id.tv_action);
        TextView contactNumberTV = (TextView) view.findViewById(R.id.tv_action_description);

        String contactNameString = cursor.getString(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_ACTION));
        String contactNumberString = cursor.getString(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_DESCRIPTION));

        contactNameTV.setText(contactNameString);
        contactNumberTV.setText(contactNumberString);
    }
}
