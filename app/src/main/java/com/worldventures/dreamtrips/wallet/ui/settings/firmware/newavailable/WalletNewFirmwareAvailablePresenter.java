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
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.puck_connection.WalletPuckConnectionPath;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.functions.Action1;

import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.checkStorageAvailability;
import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.getAppropriateFirmwareFile;

public class WalletNewFirmwareAvailablePresenter extends WalletPresenter<WalletNewFirmwareAvailablePresenter.Screen, Parcelable> {

   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   @Inject Navigator navigator;
   @Inject ActivityRouter activityRouter;
   @Inject TemporaryStorage temporaryStorage;

   private FirmwareInfo firmwareInfo;
   private final SmartCard smartCard;
   private final FirmwareUpdateData firmwareUpdateData;

   public WalletNewFirmwareAvailablePresenter(SmartCard smartCard, FirmwareUpdateData firmwareUpdateData, Context context, Injector injector) {
      super(context, injector);
      this.smartCard = smartCard;
      this.firmwareUpdateData = firmwareUpdateData;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeLatestAvailableFirmware();
      observeSmartCard();

      sendAnalyticAction();
   }

   private void observeSmartCard() {
      if (smartCard == null) {
         smartCardInteractor.activeSmartCardPipe()
               .observeWithReplay()
               .compose(new ActionStateToActionTransformer<>())
               .compose(bindViewIoToMainComposer())
               .subscribe(ErrorSubscriberWrapper.<ActiveSmartCardCommand>forView(getView().provideOperationDelegate())
                     .onNext(new Action1<ActiveSmartCardCommand>() {
                        @Override
                        public void call(ActiveSmartCardCommand command) {
                           WalletNewFirmwareAvailablePresenter.this.getView()
                                 .currentFirmwareInfo(command.getResult().firmwareVersion());
                        }
                     })
                     .onFail(ErrorHandler.create(getContext()))
                     .wrap());

      } else {
         getView().currentFirmwareInfo();
      }
   }

   private void observeLatestAvailableFirmware() {
      firmwareInteractor.firmwareInfoPipe().observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               firmwareInfo = command.getResult().firmwareInfo();
               if (firmwareInfo != null) {
                  getView().availableFirmwareInfo(firmwareInfo);
                  if (!firmwareInfo.isCompatible()) {
                     getView().requiredLatestDtAppVersion();
                  }
               }
            });
   }

   private void sendAnalyticAction() {
      if (smartCard == null) {
         Observable.zip(firmwareInteractor.firmwareInfoPipe().observeSuccessWithReplay().take(1),
               smartCardInteractor.activeSmartCardPipe().observeSuccessWithReplay().take(1),
               (firmwareCommand, smartCardCommand) -> {
                  return new ViewSdkUpdateAction(firmwareCommand.getResult().firmwareInfo().firmwareVersion(),
                        smartCardCommand.getResult().firmwareVersion().firmwareVersion(),
                        !firmwareCommand.getResult().firmwareInfo().isCompatible());
               }).subscribe(viewSdkUpdateAction -> {
            analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(smartCard, viewSdkUpdateAction));
         });
      } else {
         if (firmwareUpdateData.firmwareInfo() != null) {
            analyticsInteractor.walletAnalyticsCommandPipe().send(
                  new WalletAnalyticsCommand(smartCard,
                        new ViewSdkUpdateAction(
                              firmwareUpdateData.firmwareInfo().firmwareVersion(),
                              smartCard.firmwareVersion().firmwareVersion(),
                              !firmwareUpdateData.firmwareInfo().isCompatible())
                  ));
         }
      }
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
      try {
         checkStorageAvailability(getContext(), firmwareInfo.fileSize());
         File file = getAppropriateFirmwareFile(getContext());
         downloadFile(file.getAbsolutePath());
      } catch (WalletFilesUtils.NotEnoughSpaceException e) {
         getView().insufficientSpace(e.getMissingByteSpace());

         WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(smartCard, new InsufficientStorageAction());
         analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
      }
   }

   void downloadFile(String filePath) {
      navigator.go(new WalletPuckConnectionPath(smartCard, firmwareUpdateData, firmwareInfo, filePath));
   }

   public interface Screen extends WalletScreen {

      void requiredLatestDtAppVersion();

      void availableFirmwareInfo(FirmwareInfo firmwareInfo);

      void currentFirmwareInfo(@Nullable SmartCardFirmware version);

      void currentFirmwareInfo();

      void insufficientSpace(long missingByteSpace);
   }
}
