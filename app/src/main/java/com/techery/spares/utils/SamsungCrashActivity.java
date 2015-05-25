package com.techery.spares.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SamsungCrashActivity extends AppCompatActivity {

    @InjectView(R.id.test_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.inject(this);
        //
        toolbar.inflateMenu(R.menu.menu_test);
        setSupportActionBar(toolbar);
    }
}
