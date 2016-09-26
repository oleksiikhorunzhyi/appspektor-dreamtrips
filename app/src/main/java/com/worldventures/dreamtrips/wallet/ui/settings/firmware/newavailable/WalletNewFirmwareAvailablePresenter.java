package com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.donwload.WalletDownloadFirmwarePath;

import java.io.File;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getAvailableBytes;
import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getMostAppropriateCacheStorage;

public class WalletNewFirmwareAvailablePresenter extends WalletPresenter<WalletNewFirmwareAvailablePresenter.Screen, Parcelable> {

   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor smartCardInteractor;

   @Inject Navigator navigator;
   @Inject ActivityRouter activityRouter;
   @Inject TemporaryStorage temporaryStorage;

   private FirmwareInfo firmwareInfo;

   public WalletNewFirmwareAvailablePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeLatestAvailableFirmware();
      observeSmartCard();
   }

   private void observeSmartCard() {
      smartCardInteractor.activeSmartCardPipe().createObservableResult(new GetActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(commandActionState -> {
               getView().currentFirmwareInfo(commandActionState.getResult().sdkVersion());
            });
   }

   private void observeLatestAvailableFirmware() {
      firmwareInteractor.firmwareInfoPipe().observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               firmwareInfo = command.getResult();
               getView().availableFirmwareInfo(firmwareInfo);
               if (!firmwareInfo.isCompatible()) {
                  getView().requiredLatestDtAppVersion();
               }
            });
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
      File mostAppropriateStorage = getMostAppropriateCacheStorage(getContext());
      if (checkStorageAvailability(mostAppropriateStorage)) {
         downloadFile(getPathForFirmware(mostAppropriateStorage, firmwareInfo.downloadUrl()));
      }
   }

   void downloadFile(String filePath) {
      navigator.go(new WalletDownloadFirmwarePath(firmwareInfo,filePath));
   }

   private boolean checkStorageAvailability(File file) {
      long availableBytes = getAvailableBytes(file, temporaryStorage);
      boolean enoughSpace = availableBytes > firmwareInfo.byteSize();
      if (!enoughSpace) {
         getView().insufficientSpace(firmwareInfo.byteSize() - availableBytes);
      }
      return enoughSpace;
   }

   private String getPathForFirmware(File filePath, String url) {
      return filePath.getAbsolutePath() + File.separator + CachedEntity.getFileName(url);
   }

   public interface Screen extends WalletScreen {

      void requiredLatestDtAppVersion();

      void availableFirmwareInfo(FirmwareInfo firmwareInfo);

      void currentFirmwareInfo(String version);

      void insufficientSpace(long missingByteSpace);

   }
}