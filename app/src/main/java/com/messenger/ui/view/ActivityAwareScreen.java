package com.messenger.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface ActivityAwareScreen extends MvpView {
    AppCompatActivity getActivity();
    Context getContext();
    boolean onCreateOptionsMenu(Menu menu);
    boolean onOptionsItemSelected(MenuItem item);
    void onPrepareOptionsMenu(Menu menu);
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void onDestroy();
}
