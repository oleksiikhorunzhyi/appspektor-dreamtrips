package com.messenger.ui.presenter;


import android.os.Parcelable;

import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.ui.view.MessengerScreen;

import java.util.concurrent.TimeUnit;

public abstract class MessengerPresenterImpl<V extends MessengerScreen, S extends Parcelable>
        extends BaseViewStateMvpPresenter<V, S> implements MessengerPresenter<V, S> {

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        MessengerConnector.getInstance().subscribe()
                .compose(bindView())
                .throttleLast(50, TimeUnit.MILLISECONDS)
                .subscribe(connectionStatus -> {
                    if (isViewAttached()) {
                        getView().onConnectionChanged(connectionStatus);
                    }
                });
    }

    @Override
    public void onDisconnectedOverlayClicked() {
        MessengerConnector.getInstance().connect();
    }
}
