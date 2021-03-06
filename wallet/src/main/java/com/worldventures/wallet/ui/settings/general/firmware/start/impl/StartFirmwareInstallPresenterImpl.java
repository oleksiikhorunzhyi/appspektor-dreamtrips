package com.worldventures.wallet.ui.settings.general.firmware.start.impl;


import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.firmware.FirmwareUpdateType;
import com.worldventures.wallet.service.firmware.command.PrepareForUpdateCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.start.StartFirmwareInstallScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class StartFirmwareInstallPresenterImpl extends WalletPresenterImpl<StartFirmwareInstallScreen> implements StartFirmwareInstallPresenter {

   private final FirmwareInteractor firmwareInteractor;

   public StartFirmwareInstallPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FirmwareInteractor firmwareInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.firmwareInteractor = firmwareInteractor;
   }

   @Override
   public void attachView(StartFirmwareInstallScreen view) {
      super.attachView(view);
      firmwareInteractor.prepareForUpdatePipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationPrepareForUpdate())
                  .onSuccess(command -> cardPrepared(command.getResult()))
                  .create());

      prepareForUpdate();
   }

   @Override
   public void prepareForUpdate() {
      firmwareInteractor.prepareForUpdatePipe().send(new PrepareForUpdateCommand());
   }

   private void cardPrepared(FirmwareUpdateType type) {
      if (type == FirmwareUpdateType.CRITICAL) {
         goToConnectionInstructions();
      } else {
         goToFWUpdate();
      }
   }

   private void goToConnectionInstructions() {
      getNavigator().goForceUpdatePowerOn();
   }

   private void goToFWUpdate() {
      getNavigator().goNewFirmwareAvailable();
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void finish() {
      getNavigator().finish();
   }
}
