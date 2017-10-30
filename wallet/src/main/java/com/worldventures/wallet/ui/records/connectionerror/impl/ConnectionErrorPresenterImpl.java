package com.worldventures.wallet.ui.records.connectionerror.impl;


import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.records.connectionerror.ConnectionErrorPresenter;
import com.worldventures.wallet.ui.records.connectionerror.ConnectionErrorScreen;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;

public class ConnectionErrorPresenterImpl extends WalletPresenterImpl<ConnectionErrorScreen> implements ConnectionErrorPresenter {

   private final SmartCardInteractor smartCardInteractor;

   public ConnectionErrorPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
   }

   @Override
   public void attachView(ConnectionErrorScreen view) {
      super.attachView(view);
      observeConnection();
   }

   private void observeConnection() {
      smartCardInteractor.deviceStatePipe()
            .observeSuccessWithReplay()
            .throttleLast(1, TimeUnit.SECONDS)
            .map(command -> command.getResult().connectionStatus())
            .distinctUntilChanged()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(connectionStatus -> {
               if (connectionStatus.isConnected()) {
                  getNavigator().goWizardCharging();
               }
            });

      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
