package com.worldventures.wallet.ui.common.base;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import rx.Observable;


public interface WalletNetworkDelegate {

   void setup(WalletScreen view);

   boolean isAvailable();

   Observable<Boolean> observeConnectedState();
}
