package com.dalydays.android.textecutor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

/**
 * Created by edaly on 6/19/17.
 */

public class ActionActivity extends FragmentActivity {

    private ListView mActionList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        mActionList = (ListView) findViewById(R.id.action_list);
    }
}
