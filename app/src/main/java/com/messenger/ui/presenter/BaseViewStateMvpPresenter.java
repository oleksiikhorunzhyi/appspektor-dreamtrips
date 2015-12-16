package com.messenger.ui.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import icepick.Icepick;
import icepick.State;

public abstract class BaseViewStateMvpPresenter<V extends MvpView, S extends Parcelable> extends MvpBasePresenter<V>
        implements ActivityAwareViewStateMvpPresenter<V, S> {

    @State S state;

    @Override public void onSaveInstanceState(Bundle bundle) {
        Icepick.saveInstanceState(this, bundle);
    }

    @Override public void onRestoreInstanceState(Bundle instanceState) {
        Icepick.restoreInstanceState(this, instanceState);
        applyViewState();
    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public void onDetachedFromWindow() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override public S getViewState() {
        return state;
    }
}
