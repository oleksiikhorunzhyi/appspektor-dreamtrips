package com.messenger.ui.view;

import android.view.ViewGroup;

public interface MessengerScreen extends ActivityAwareScreen {
    void showDisconnectedOverlay();
    void hideDisconnectedOverlay();
}
