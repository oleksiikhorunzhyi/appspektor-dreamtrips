package com.messenger.ui.presenter;

import android.content.Intent;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface ActivityAwareViewStateMvpPresenter<V extends MvpView, S extends Parcelable>
        extends ViewStateMvpPresenter<V, S> {
    boolean onCreateOptionsMenu(Menu menu);
    boolean onOptionsItemSelected(MenuItem item);
    void onPrepareOptionsMenu(Menu menu);
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void onDestroy();
}
