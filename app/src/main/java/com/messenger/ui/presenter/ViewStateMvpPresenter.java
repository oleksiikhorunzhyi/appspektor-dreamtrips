package com.messenger.ui.presenter;

import android.os.Bundle;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import com.messenger.ui.viewstate.BaseRestorableViewState;

public interface ViewStateMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void onSaveInstanceState(Bundle bundle);
    void onRestoreInstanceState(Bundle instanceState);
    BaseRestorableViewState<V> getViewState();
    void onNewViewState();
    void applyViewState();
}
