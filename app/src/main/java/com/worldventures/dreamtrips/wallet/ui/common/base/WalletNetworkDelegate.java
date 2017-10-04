package com.worldventures.dreamtrips.wallet.ui.common.base;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import rx.Observable;


public interface WalletNetworkDelegate {

   void setup(WalletScreen view);

   boolean isAvailable();

   Observable<Boolean> observeConnectedState();
}
