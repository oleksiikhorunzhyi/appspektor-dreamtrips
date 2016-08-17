package com.messenger.ui.view.layout;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.messenger.synchmechanism.SyncStatus;

public interface MessengerScreen extends MvpView {
   void onConnectionChanged(SyncStatus syncStatus);
}
