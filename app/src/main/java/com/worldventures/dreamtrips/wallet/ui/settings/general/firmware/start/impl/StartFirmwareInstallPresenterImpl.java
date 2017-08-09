package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.impl;


import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareUpdateType;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class StartFirmwareInstallPresenterImpl extends WalletPresenterImpl<StartFirmwareInstallScreen> implements StartFirmwareInstallPresenter {

   private final FirmwareInteractor firmwareInteractor;

   public StartFirmwareInstallPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FirmwareInteractor firmwareInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.firmwareInteractor = firmwareInteractor;
   }

   @Override
   public void attachView(StartFirmwareInstallScreen view) {
      super.attachView(view);
      firmwareInteractor.prepareForUpdatePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
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
