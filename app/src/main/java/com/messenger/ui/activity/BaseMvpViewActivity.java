package com.messenger.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

@Deprecated
public abstract class BaseMvpViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View screen = createScreen();
        screen.setId(android.R.id.primary);
        setContentView(screen);
    }

    abstract View createScreen();
}
