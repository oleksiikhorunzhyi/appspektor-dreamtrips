package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.impl;


import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareUpdateType;
import com.worldventures.dreamtrips.wallet.service.firmware.command.PrepareForUpdateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallScreen;

public class StartFirmwareInstallPresenterImpl extends WalletPresenterImpl<StartFirmwareInstallScreen> implements StartFirmwareInstallPresenter {

   private final ErrorHandlerFactory errorHandlerFactory;
   private final FirmwareInteractor firmwareInteractor;

   public StartFirmwareInstallPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, ErrorHandlerFactory errorHandlerFactory, FirmwareInteractor firmwareInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.errorHandlerFactory = errorHandlerFactory;
      this.firmwareInteractor = firmwareInteractor;
   }

   @Override
   public void attachView(StartFirmwareInstallScreen view) {
      super.attachView(view);
      firmwareInteractor.prepareForUpdatePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<PrepareForUpdateCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> cardPrepared(command.getResult()))
                  .onFail(errorHandlerFactory.errorHandler(command -> prepareForUpdate()))
                  .wrap());

      prepareForUpdate();
   }

   private void prepareForUpdate() {
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

   public interface Screen extends WalletScreen {
   }
}
