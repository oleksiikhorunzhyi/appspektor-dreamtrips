package com.messenger.ui.presenter;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface ActivityAwareViewStateMvpPresenter<V extends MvpView> extends ViewStateMvpPresenter<V> {
    boolean onCreateOptionsMenu(Menu menu);
    boolean onOptionsItemSelected(MenuItem item);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
