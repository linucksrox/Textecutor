package com.dalydays.android.textecutor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by edaly on 6/19/17.
 */

public class ActionActivity extends FragmentActivity {

    private ListView mActionList;
    ActionsCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        mActionList = (ListView) findViewById(R.id.action_list);
        mActionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //showDeleteConfirmationDialog(id);
            }
        });

        mCursorAdapter = new ActionsCursorAdapter(this, null);
        mActionList.setAdapter(mCursorAdapter);
    }
}
