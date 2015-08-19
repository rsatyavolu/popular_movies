package com.rsatyavolu.nanodegree.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_toolbar);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsPreference())
                .commit();
    }
}