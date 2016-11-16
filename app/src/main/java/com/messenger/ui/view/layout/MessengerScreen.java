package com.messenger.ui.view.layout;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.messenger.synchmechanism.SyncStatus;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;

import rx.Observable;

public interface MessengerScreen extends MvpView {
   void initDisconnectedOverlay(Observable<ConnectionState> syncStatus);
}
