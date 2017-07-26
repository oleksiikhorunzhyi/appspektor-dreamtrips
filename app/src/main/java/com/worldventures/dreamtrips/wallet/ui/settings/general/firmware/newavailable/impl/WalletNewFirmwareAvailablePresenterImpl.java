package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.impl;


import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.InsufficientStorageAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.ViewSdkUpdateAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailablePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailableScreen;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;

import static com.worldventures.dreamtrips.wallet.util.WalletFilesUtils.checkStorageAvailability;

public class WalletNewFirmwareAvailablePresenterImpl extends WalletPresenterImpl<WalletNewFirmwareAvailableScreen> implements WalletNewFirmwareAvailablePresenter{

   private final FirmwareInteractor firmwareInteractor;
   private final AnalyticsInteractor analyticsInteractor;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;

   public WalletNewFirmwareAvailablePresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, smartCardInteractor, networkService);
      this.firmwareInteractor = firmwareInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
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
      getNavigator().goPlayStore(getView().getViewContext());
   }

   @Override
   public void openSettings() {
      getNavigator().goSettings(getView().getViewContext());
   }

   @Override
   public void downloadButtonClicked() {
      firmwareInteractor.firmwareInfoCachedPipe().createObservable(FirmwareInfoCachedCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FirmwareInfoCachedCommand>()
                  .onSuccess(command -> checkStoreAndNavigateToDownLoadScreen(command.getResult())));
   }

   private void checkStoreAndNavigateToDownLoadScreen(FirmwareUpdateData firmwareUpdateData) {
      try {
         checkStorageAvailability(getView().getViewContext(), firmwareUpdateData.firmwareInfo().fileSize());
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

   @Override
   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }
}
