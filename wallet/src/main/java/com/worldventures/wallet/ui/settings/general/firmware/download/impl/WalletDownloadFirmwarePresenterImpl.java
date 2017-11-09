package com.worldventures.wallet.ui.settings.general.firmware.download.impl;


import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.wallet.analytics.firmware.action.DownloadingUpdateAction;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.firmware.command.DownloadFirmwareCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwarePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwareScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WalletDownloadFirmwarePresenterImpl extends WalletPresenterImpl<WalletDownloadFirmwareScreen> implements WalletDownloadFirmwarePresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;
   private final FirmwareInteractor firmwareInteractor;

   private DownloadFirmwareCommand action;

   public WalletDownloadFirmwarePresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, FirmwareInteractor firmwareInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.analyticsInteractor = analyticsInteractor;
      this.firmwareInteractor = firmwareInteractor;
   }

   @Override
   public void attachView(WalletDownloadFirmwareScreen view) {
      super.attachView(view);
      trackScreen();
      observeDownload();
      downloadFirmware();
   }

   private void observeDownload() {
      firmwareInteractor.downloadFirmwarePipe()
            .observe()
            .compose(new ActionPipeCacheWiper<>(firmwareInteractor.downloadFirmwarePipe()))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationDownload())
                  .onSuccess(event -> openPreInstallationChecks())
                  .create());
   }

   @Override
   public void downloadFirmware() {
      action = new DownloadFirmwareCommand();
      firmwareInteractor.downloadFirmwarePipe().send(action);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   private void trackScreen() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new DownloadingUpdateAction()));
   }

   private void openPreInstallationChecks() {
      getNavigator().goWalletFirmwareChecks();
   }

   @Override
   public void cancelDownload() {
      if (action != null) {
         firmwareInteractor.downloadFirmwarePipe().cancel(action);
      }
      getNavigator().goBack();
   }
}
