package com.worldventures.wallet.ui.settings.general.firmware.newavailable.impl;


import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.wallet.analytics.firmware.action.InsufficientStorageAction;
import com.worldventures.wallet.analytics.firmware.action.ViewSdkUpdateAction;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailablePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailableScreen;
import com.worldventures.wallet.util.GuaranteedProgressVisibilityTransformer;
import com.worldventures.wallet.util.WalletFilesUtils;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.wallet.util.WalletFilesUtils.checkStorageAvailability;

public class WalletNewFirmwareAvailablePresenterImpl extends WalletPresenterImpl<WalletNewFirmwareAvailableScreen> implements WalletNewFirmwareAvailablePresenter {

   private final FirmwareInteractor firmwareInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public WalletNewFirmwareAvailablePresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FirmwareInteractor firmwareInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.firmwareInteractor = firmwareInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletNewFirmwareAvailableScreen view) {
      super.attachView(view);
      trackScreen();

      observeFirmwareInfo();
      fetchFirmwareInfo();
   }

   private void observeFirmwareInfo() {
      firmwareInteractor.fetchFirmwareUpdateDataCommandActionPipe()
            .observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> bindDataToView(command.getResult()))
                  .create());
   }

   private void bindDataToView(FirmwareUpdateData firmwareUpdateData) {
      final FirmwareInfo firmwareInfo = firmwareUpdateData.getFirmwareInfo();
      final boolean isCompatible = firmwareInfo != null && firmwareInfo.isCompatible();

      getView().currentFirmwareInfo(firmwareUpdateData.getCurrentFirmwareVersion(), firmwareInfo, isCompatible);
   }

   private void trackScreen() {
      analyticsInteractor.walletFirmwareAnalyticsPipe()
            .send(new WalletFirmwareAnalyticsCommand(new ViewSdkUpdateAction()));
   }

   @Override
   public void fetchFirmwareInfo() {
      firmwareInteractor.fetchFirmwareUpdateDataCommandActionPipe().send(new FetchFirmwareUpdateDataCommand());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void openMarket() {
      getNavigator().goPlayStore();
   }

   @Override
   public void openSettings() {
      getNavigator().goSettings();
   }

   @Override
   public void downloadButtonClicked() {
      firmwareInteractor.firmwareInfoCachedPipe().createObservable(FirmwareInfoCachedCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<FirmwareInfoCachedCommand>()
                  .onSuccess(command -> checkStoreAndNavigateToDownLoadScreen(command.getResult())));
   }

   private void checkStoreAndNavigateToDownLoadScreen(FirmwareUpdateData firmwareUpdateData) {
      try {
         checkStorageAvailability(getView().getViewContext(), firmwareUpdateData.getFirmwareInfo().fileSize());
         goDownloadFile();
      } catch (WalletFilesUtils.NotEnoughSpaceException e) {
         getView().insufficientSpace(e.getMissingByteSpace());

         analyticsInteractor.walletFirmwareAnalyticsPipe()
               .send(new WalletFirmwareAnalyticsCommand(new InsufficientStorageAction()));
      }
   }

   private void goDownloadFile() {
      getNavigator().goPuckConnection();
   }
}
