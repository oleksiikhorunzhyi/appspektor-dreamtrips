package com.worldventures.wallet.ui.common.base.screen;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.wallet.domain.entity.ConnectionStatus;

public interface WalletScreen extends MvpView, RxLifecycleView {

   void showConnectionStatus(ConnectionStatus connectionStatus);

   void showHttpConnectionStatus(boolean connected);
}