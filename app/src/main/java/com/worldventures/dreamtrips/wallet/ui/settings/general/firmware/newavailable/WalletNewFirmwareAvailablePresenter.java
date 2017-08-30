package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.InsufficientStorageAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.ViewSdkUpdateAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionPath;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.checkStorageAvailability;

public class WalletNewFirmwareAvailablePresenter extends WalletPresenter<WalletNewFirmwareAvailablePresenter.Screen, Parcelable> {

   @Inject FirmwareInteractor firmwareInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   @Inject Navigator navigator;
   @Inject ActivityRouter activityRouter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   public WalletNewFirmwareAvailablePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();

      observeFirmwareInfo();
      fetchFirmwareInfo();
   }

   private void observeFirmwareInfo() {
      firmwareInteractor.fetchFirmwareUpdateDataCommandActionPipe()
            .observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> bindDataToView(command.getResult()))
                  .create());
   }

   private void bindDataToView(FirmwareUpdateData firmwareUpdateData) {
      final FirmwareInfo firmwareInfo = firmwareUpdateData.firmwareInfo();
      final boolean isCompatible = firmwareInfo != null && firmwareInfo.isCompatible();

      getView().currentFirmwareInfo(firmwareUpdateData.currentFirmwareVersion(), firmwareInfo, isCompatible);
   }

   private void trackScreen() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new ViewSdkUpdateAction()));
   }

   void fetchFirmwareInfo() {
      firmwareInteractor.fetchFirmwareUpdateDataCommandActionPipe().send(new FetchFirmwareUpdateDataCommand());
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
      firmwareInteractor.firmwareInfoCachedPipe().createObservable(FirmwareInfoCachedCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FirmwareInfoCachedCommand>()
                  .onSuccess(command -> checkStoreAndNavigateToDownLoadScreen(command.getResult())));
   }

   private void checkStoreAndNavigateToDownLoadScreen(FirmwareUpdateData firmwareUpdateData) {
      try {
         checkStorageAvailability(getContext(), firmwareUpdateData.firmwareInfo().fileSize());
         goDownloadFile();
      } catch (WalletFilesUtils.NotEnoughSpaceException e) {
         getView().insufficientSpace(e.getMissingByteSpace());

         analyticsInteractor.walletFirmwareAnalyticsPipe()
               .send(new WalletFirmwareAnalyticsCommand(new InsufficientStorageAction()));
      }
   }

   private void goDownloadFile() {
      navigator.go(new WalletPuckConnectionPath());
   }

   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }

   public interface Screen extends WalletScreen {

      OperationView<FetchFirmwareUpdateDataCommand> provideOperationView();

      void currentFirmwareInfo(@Nullable SmartCardFirmware version, FirmwareInfo firmwareInfo, boolean isCompatible);

      void insufficientSpace(long missingByteSpace);
   }
}
