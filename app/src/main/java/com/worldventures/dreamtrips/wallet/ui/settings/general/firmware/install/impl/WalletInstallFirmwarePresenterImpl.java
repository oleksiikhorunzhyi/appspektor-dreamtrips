package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.impl;


import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.InstallingUpdateAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.RetryInstallUpdateAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.firmware.command.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwareScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwareState;

public class WalletInstallFirmwarePresenterImpl extends WalletPresenterImpl<WalletInstallFirmwareScreen> implements WalletInstallFirmwarePresenter {

   private final FirmwareInteractor firmwareInteractor;
   private final AnalyticsInteractor analyticsInteractor;
   private final ErrorHandlerFactory errorHandlerFactory;

   private WalletInstallFirmwareState state;

   public WalletInstallFirmwarePresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      super(navigator, smartCardInteractor, networkService);
      this.firmwareInteractor = firmwareInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.errorHandlerFactory = errorHandlerFactory;
   }

   @Override
   public void attachView(WalletInstallFirmwareScreen view) {
      super.attachView(view);
      trackScreen();

      firmwareInteractor.installFirmwarePipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper(firmwareInteractor.installFirmwarePipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<InstallFirmwareCommand>forView(getView())
                  .onSuccess(command -> openSuccess(command.getResult()))
                  .onProgress((command, integer) -> getView().showInstallingStatus(command.getCurrentStep(),
                        InstallFirmwareCommand.INSTALL_FIRMWARE_TOTAL_STEPS, integer))
                  .onFail(errorHandlerFactory.errorHandler())
                  .wrap()
                  .onStart(installFirmwareCommand -> state.started = true)
            );
      if (!state.started) {
         install();
      }
   }

   private void trackScreen() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new InstallingUpdateAction()));
   }

//   TODO : uncomment on implement
//   @Override
//   public void onNewViewState() {
//      state = new WalletInstallFirmwareState();
//   }

   protected void install() {
      getView().showProgress(null);
      getView().showInstallingStatus(0, InstallFirmwareCommand.INSTALL_FIRMWARE_TOTAL_STEPS, 0);
      firmwareInteractor.installFirmwarePipe().send(new InstallFirmwareCommand());
   }

   private void openSuccess(FirmwareUpdateData firmwareUpdateData) {
      if (!firmwareUpdateData.factoryResetRequired()) {
         getNavigator().goWalletSuccessFirmwareInstall(firmwareUpdateData);
      } else {
         getNavigator().goWalletSuccessFirmwareInstallAfterReset(firmwareUpdateData);
      }
   }

   @Override
   public void cancelReinstall() {
      sendRetryAnalyticAction(false);
      getNavigator().finish();
   }

   @Override
   public void retry() {
      install();
      sendRetryAnalyticAction(true);
   }

   private void sendRetryAnalyticAction(boolean retry) {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new RetryInstallUpdateAction(retry)));
   }
}
