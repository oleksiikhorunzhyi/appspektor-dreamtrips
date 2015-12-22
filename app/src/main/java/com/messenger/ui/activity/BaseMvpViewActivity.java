package com.messenger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.messenger.ui.view.ActivityAwareScreen;

public abstract class BaseMvpViewActivity<T extends View & ActivityAwareScreen> extends AppCompatActivity {

    protected T screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screen = createScreen();
        screen.setId(android.R.id.primary);
        setContentView(screen);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return screen.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return screen.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        screen.onPrepareOptionsMenu(menu);
        // TODO return the same value as screen
        return true;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        screen.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        screen.onDestroy();
    }

    abstract T createScreen();
}
