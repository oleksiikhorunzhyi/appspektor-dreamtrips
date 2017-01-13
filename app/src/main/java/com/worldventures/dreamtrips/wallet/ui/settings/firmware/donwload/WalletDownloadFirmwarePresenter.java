package com.worldventures.dreamtrips.wallet.ui.settings.firmware.donwload;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.wallet.analytics.DownloadingUpdateAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.SCFirmwareFacade;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion.WalletFirmwareChecksPath;

import javax.inject.Inject;

import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile;

public class WalletDownloadFirmwarePresenter extends WalletPresenter<WalletDownloadFirmwarePresenter.Screen, Parcelable> {

   @Inject DownloadFileInteractor fileInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SCFirmwareFacade firmwareFacade;

   @Inject Navigator navigator;
   private DownloadFileCommand action;

   public WalletDownloadFirmwarePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      firmwareFacade.takeFirmwareInfo()
            .compose(bindViewIoToMainComposer())
            .subscribe(this::downloadFirmware, throwable -> Timber.e(throwable, ""));
   }

   private void downloadFirmware(FirmwareUpdateData firmwareUpdateData) {
      action = new DownloadFileCommand(getAppropriateFirmwareFile(getContext()), firmwareUpdateData.firmwareInfo().url());
      fileInteractor.getDownloadFileCommandPipe()
            .createObservable(action)
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<DownloadFileCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(event -> openPreInstallationChecks())
                  .onFail(ErrorHandler.create(getContext(), it -> navigator.goBack()))
                  .wrap());

      WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new DownloadingUpdateAction(
            firmwareUpdateData.smartCardId()));
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   private void openPreInstallationChecks() {
      navigator.withoutLast(new WalletFirmwareChecksPath());
   }

   void cancelDownload() {
      if (action != null) {
         fileInteractor.getDownloadFileCommandPipe().cancel(action);
      }
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }

}
