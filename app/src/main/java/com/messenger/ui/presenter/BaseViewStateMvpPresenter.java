package com.messenger.ui.presenter;

import android.os.Bundle;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import icepick.Icepick;
import icepick.State;
import com.messenger.ui.viewstate.BaseRestorableViewState;

public abstract class BaseViewStateMvpPresenter<V extends MvpView> extends MvpBasePresenter<V>
        implements ViewStateMvpPresenter<V> {

    @State
    BaseRestorableViewState<V> state;

    @Override public void onSaveInstanceState(Bundle bundle) {
        Icepick.saveInstanceState(this, bundle);
    }

    @Override public void onRestoreInstanceState(Bundle instanceState) {
        Icepick.restoreInstanceState(this, instanceState);
        applyViewState();
    }

    @Override public BaseRestorableViewState<V> getViewState() {
        return state;
    }
}
