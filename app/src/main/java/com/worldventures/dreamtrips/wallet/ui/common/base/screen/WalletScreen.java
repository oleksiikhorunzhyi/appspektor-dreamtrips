package com.worldventures.dreamtrips.wallet.ui.common.base.screen;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

public interface WalletScreen extends MvpView {

   @Deprecated
   OperationScreen provideOperationDelegate();

   void showConnectionStatus(SmartCard.ConnectionStatus connectionStatus);
}