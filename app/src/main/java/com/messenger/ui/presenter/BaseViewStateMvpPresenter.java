package com.messenger.ui.presenter;

import android.os.Bundle;
import android.os.Parcelable;

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

    @Override public S getViewState() {
        return state;
    }
}
