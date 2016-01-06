package com.messenger.ui.presenter;

import android.os.Parcelable;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface MessengerPresenter<V extends MvpView, S extends Parcelable>
        extends ActivityAwareViewStateMvpPresenter<V, S> {
    void onDisconnectedOverlayClicked();
}
