package com.worldventures.dreamtrips.wallet.ui.settings.firmware.donwload;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.wallet.analytics.DownloadingUpdateAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion.WalletFirmwareChecksPath;

import java.io.File;

import javax.inject.Inject;

public class WalletDownloadFirmwarePresenter extends WalletPresenter<WalletDownloadFirmwarePresenter.Screen, Parcelable> {

   private final FirmwareInfo firmwareInfo;
   private final String firmwareFilePath;

   @Inject DownloadFileInteractor fileInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   @Inject Navigator navigator;
   private DownloadFileCommand action;
   private final SmartCard smartCard;

   public WalletDownloadFirmwarePresenter(SmartCard smartCard, Context context, Injector injector, FirmwareInfo firmwareInfo, String firmwareFilePath) {
      super(context, injector);
      this.firmwareInfo = firmwareInfo;
      this.firmwareFilePath = firmwareFilePath;
      this.smartCard = smartCard;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      action = new DownloadFileCommand(new File(firmwareFilePath), firmwareInfo.url());
      fileInteractor.getDownloadFileCommandPipe()
            .createObservable(action)
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<DownloadFileCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(event -> openPreInstallationChecks())
                  .onFail(ErrorHandler.create(getContext(), it -> navigator.goBack()))
                  .wrap());

      WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(smartCard, new DownloadingUpdateAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   private void openPreInstallationChecks() {
      navigator.withoutLast(new WalletFirmwareChecksPath(smartCard, firmwareFilePath, firmwareInfo));
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
