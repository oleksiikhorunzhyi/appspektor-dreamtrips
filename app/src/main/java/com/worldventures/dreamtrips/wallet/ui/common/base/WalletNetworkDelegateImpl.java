package com.worldventures.dreamtrips.wallet.ui.common.base;


import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WalletNetworkDelegateImpl implements WalletNetworkDelegate {

   private final WalletNetworkService networkService;

   public WalletNetworkDelegateImpl(WalletNetworkService networkService) {
      this.networkService = networkService;
   }

   @Override
   public void setup(WalletScreen view) {
      networkService.observeConnectedState()
            .throttleLast(1, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .compose(view.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::showHttpConnectionStatus);
   }

   @Override
   public boolean isAvailable() {
      return networkService.isAvailable();
   }

   @Override
   public Observable<Boolean> observeConnectedState() {
      return networkService.observeConnectedState();
   }
}
