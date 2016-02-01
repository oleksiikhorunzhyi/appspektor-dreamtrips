package com.messenger.ui.view.layout;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.messenger.synchmechanism.ConnectionStatus;

public interface MessengerScreen extends MvpView {
    boolean onBackPressed();
    void onConnectionChanged(ConnectionStatus connectionStatus);
}
