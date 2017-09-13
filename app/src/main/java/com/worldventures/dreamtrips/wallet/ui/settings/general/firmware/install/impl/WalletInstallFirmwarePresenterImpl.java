package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.impl;


import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.InstallingUpdateAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.RetryInstallUpdateAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.command.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwareScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WalletInstallFirmwarePresenterImpl extends WalletPresenterImpl<WalletInstallFirmwareScreen> implements WalletInstallFirmwarePresenter {

   private final FirmwareInteractor firmwareInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public WalletInstallFirmwarePresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FirmwareInteractor firmwareInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.firmwareInteractor = firmwareInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletInstallFirmwareScreen view) {
      super.attachView(view);
      trackScreen();

      firmwareInteractor.installFirmwarePipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(firmwareInteractor.installFirmwarePipe()))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationInstall())
                  .onStart(installFirmwareCommand -> getView().setInstallStarted(true))
                  .onSuccess(command -> openSuccess(command.getResult()))
                  .onProgress((cmd, progress) -> getView().showInstallingStatus(cmd.getCurrentStep(),
                        InstallFirmwareCommand.INSTALL_FIRMWARE_TOTAL_STEPS, progress))
                  .create()
            );
      if (!getView().isInstallStarted()) {
         install();
      }
   }

   private void trackScreen() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new InstallingUpdateAction()));
   }

   @Override
   public void install() {
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
