package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;

import com.worldventures.dreamtrips.view.presentation.LaunchActivityPresentation;

public class LaunchActivity extends BaseActivity {

    LaunchActivityPresentation lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lp = new LaunchActivityPresentation(this, this);
        lp.onCreate();
        finish();
    }

}
