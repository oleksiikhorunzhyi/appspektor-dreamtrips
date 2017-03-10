package com.worldventures.dreamtrips.wallet.ui.settings.firmware.download;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.DownloadingUpdateAction;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.command.DownloadFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion.WalletFirmwareChecksPath;

import javax.inject.Inject;

public class WalletDownloadFirmwarePresenter extends WalletPresenter<WalletDownloadFirmwarePresenter.Screen, Parcelable> {

   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject Navigator navigator;

   private DownloadFirmwareCommand action;

   public WalletDownloadFirmwarePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
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
                  .onFail(ErrorHandler.create(getContext(), it -> navigator.goBack()))
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
      navigator.withoutLast(new WalletFirmwareChecksPath());
   }

   void cancelDownload() {
      if (action != null) {
         firmwareInteractor.downloadFirmwarePipe().cancel(action);
      }
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }

}
