package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.impl;


import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.DownloadingUpdateAction;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.firmware.command.DownloadFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwareScreen;

public class WalletDownloadFirmwarePresenterImpl extends WalletPresenterImpl<WalletDownloadFirmwareScreen> implements WalletDownloadFirmwarePresenter {

   private final AnalyticsInteractor analyticsInteractor;
   private final FirmwareInteractor firmwareInteractor;
   private final ErrorHandlerFactory errorHandlerFactory;

   private DownloadFirmwareCommand action;

   public WalletDownloadFirmwarePresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor, FirmwareInteractor firmwareInteractor,
         ErrorHandlerFactory errorHandlerFactory) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
      this.firmwareInteractor = firmwareInteractor;
      this.errorHandlerFactory = errorHandlerFactory;
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
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<DownloadFirmwareCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(event -> openPreInstallationChecks())
                  .onFail(errorHandlerFactory.errorHandler(it -> getNavigator().goBack()))
                  .wrap());
   }

   private void downloadFirmware() {
      action = new DownloadFirmwareCommand();
      firmwareInteractor.downloadFirmwarePipe().send(action);
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
