package com.worldventures.wallet.ui.settings.general.firmware.reset.pair.impl;

import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.firmware.command.ConnectForFirmwareUpdate;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class ForcePairKeyPresenterImpl extends WalletPresenterImpl<ForcePairKeyScreen> implements ForcePairKeyPresenter {

   private final FirmwareInteractor firmwareInteractor;

   public ForcePairKeyPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FirmwareInteractor firmwareInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.firmwareInteractor = firmwareInteractor;
   }

   @Override
   public void attachView(ForcePairKeyScreen view) {
      super.attachView(view);
      observeCreateAndConnectSmartCard();
   }

   private void observeCreateAndConnectSmartCard() {
      firmwareInteractor.connectForFirmwareUpdatePipe()
            .observeWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(new ActionPipeCacheWiper<>(firmwareInteractor.connectForFirmwareUpdatePipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationConnect())
                  .onSuccess(command -> smartCardConnected())
                  .create());
   }

   @Override
   public void tryToPairAndConnectSmartCard() {
      firmwareInteractor.connectForFirmwareUpdatePipe().send(new ConnectForFirmwareUpdate());
   }

   private void smartCardConnected() {
      getNavigator().goNewFirmwareAvailable();
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
