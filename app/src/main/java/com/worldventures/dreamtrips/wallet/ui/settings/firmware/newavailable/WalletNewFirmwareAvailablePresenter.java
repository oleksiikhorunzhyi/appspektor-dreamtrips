package com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.InsufficientStorageAction;
import com.worldventures.dreamtrips.wallet.analytics.ViewSdkUpdateAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.SCFirmwareFacade;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.puck_connection.WalletPuckConnectionPath;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import javax.inject.Inject;

import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.checkStorageAvailability;

public class WalletNewFirmwareAvailablePresenter extends WalletPresenter<WalletNewFirmwareAvailablePresenter.Screen, Parcelable> {

   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SCFirmwareFacade firmwareFacade;

   @Inject Navigator navigator;
   @Inject ActivityRouter activityRouter;
   @Inject TemporaryStorage temporaryStorage;

   public WalletNewFirmwareAvailablePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      firmwareFacade.takeFirmwareInfo()
            .compose(bindViewIoToMainComposer())
            .subscribe(firmwareUpdateData -> {
               bindDataToView(firmwareUpdateData);
               sendAnalyticAction(firmwareUpdateData);
            }, throwable -> Timber.e(throwable, ""));
   }

   private void bindDataToView(FirmwareUpdateData firmwareUpdateData) {
      getView().currentFirmwareInfo(firmwareUpdateData.currentFirmwareVersion());

      final FirmwareInfo firmwareInfo = firmwareUpdateData.firmwareInfo();
      getView().availableFirmwareInfo(firmwareInfo);
      if (!firmwareInfo.isCompatible()) {
         getView().requiredLatestDtAppVersion();
      }
   }

   private void sendAnalyticAction(FirmwareUpdateData firmwareUpdateData) {
      final FirmwareInfo firmwareInfo = firmwareUpdateData.firmwareInfo();
      analyticsInteractor.walletAnalyticsCommandPipe().send(
            new WalletAnalyticsCommand(
                  new ViewSdkUpdateAction(
                        firmwareUpdateData.smartCardId(),
                        firmwareInfo.firmwareVersion(),
                        firmwareUpdateData.currentFirmwareVersion().firmwareVersion(),
                        !firmwareInfo.isCompatible())
            ));
   }

   void goBack() {
      navigator.goBack();
   }

   void openMarket() {
      activityRouter.openMarket();
   }

   void openSettings() {
      activityRouter.openSettings();
   }

   void downloadButtonClicked() {
      firmwareFacade.takeFirmwareInfo()
            .compose(bindViewIoToMainComposer())
            .subscribe(firmwareUpdateData -> checkStoreAndNavigateToDownLoadScreen(firmwareUpdateData),
                  throwable -> Timber.e(throwable, ""));

   }

   private void checkStoreAndNavigateToDownLoadScreen(FirmwareUpdateData firmwareUpdateData) {
      try {
         checkStorageAvailability(getContext(), firmwareUpdateData.firmwareInfo().fileSize());
         goDownloadFile();
      } catch (WalletFilesUtils.NotEnoughSpaceException e) {
         getView().insufficientSpace(e.getMissingByteSpace());

         analyticsInteractor.walletAnalyticsCommandPipe()
               .send(new WalletAnalyticsCommand(new InsufficientStorageAction(firmwareUpdateData.smartCardId())));
      }
   }

   private void goDownloadFile() {
      navigator.go(new WalletPuckConnectionPath());
   }

   public interface Screen extends WalletScreen {

      void requiredLatestDtAppVersion();

      void availableFirmwareInfo(FirmwareInfo firmwareInfo);

      void currentFirmwareInfo(@Nullable SmartCardFirmware version);

      void insufficientSpace(long missingByteSpace);
   }
}
