package com.messenger.ui.view;

import android.support.v7.app.AppCompatActivity;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.messenger.synchmechanism.ConnectionStatus;

public interface MessengerScreen extends MvpView {
    void onConnectionChanged(ConnectionStatus connectionStatus);
    AppCompatActivity getActivity();
}
