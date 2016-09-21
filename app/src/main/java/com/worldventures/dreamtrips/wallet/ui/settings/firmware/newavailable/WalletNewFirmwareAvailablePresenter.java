package com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class WalletNewFirmwareAvailablePresenter extends WalletPresenter<WalletNewFirmwareAvailablePresenter.Screen, Parcelable> {

   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor smartCardInteractor;

   @Inject Navigator navigator;
   @Inject ActivityRouter activityRouter;

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
               getView().currentFirmwareInfo(commandActionState.getResult().currentSdkVersion());
            });
   }

   private void observeLatestAvailableFirmware() {
      firmwareInteractor.firmwareInfoPipe().observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(commandActionState -> {
               FirmwareInfo firmwareInfo = commandActionState.action.getResult();
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

   void downloadAndInstall() {
      //todo download and install
   }

   public interface Screen extends WalletScreen {

      void requiredLatestDtAppVersion();

      void availableFirmwareInfo(FirmwareInfo firmwareInfo);

      void currentFirmwareInfo(String version);
   }
}