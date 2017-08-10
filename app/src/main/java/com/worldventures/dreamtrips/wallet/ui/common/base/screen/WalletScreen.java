package com.worldventures.dreamtrips.wallet.ui.common.base.screen;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;

public interface WalletScreen extends MvpView {

   void showConnectionStatus(ConnectionStatus connectionStatus);

   void showHttpConnectionStatus(boolean connected);
}