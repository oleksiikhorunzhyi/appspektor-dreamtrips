package com.messenger.ui.view;

import com.messenger.synchmechanism.ConnectionStatus;

public interface MessengerScreen extends ActivityAwareScreen {
    void onConnectionChanged(ConnectionStatus connectionStatus);
}
