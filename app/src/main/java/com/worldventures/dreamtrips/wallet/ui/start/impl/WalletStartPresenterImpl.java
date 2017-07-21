package com.worldventures.dreamtrips.wallet.ui.start.impl;

import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAccessValidator;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FetchFirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartPresenter;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class WalletStartPresenterImpl extends WalletPresenterImpl<WalletStartScreen> implements WalletStartPresenter {

   private final FirmwareInteractor firmwareInteractor;
   private final WalletAccessValidator walletAccessValidator;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;


   public WalletStartPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         FirmwareInteractor firmwareInteractor, WalletAccessValidator walletAccessValidator, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, smartCardInteractor, networkService);
      this.firmwareInteractor = firmwareInteractor;
      this.walletAccessValidator = walletAccessValidator;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
   }

   @Override
   public void attachView(WalletStartScreen view) {
      super.attachView(view);
      walletAccessValidator.validate(this::onWalletAvailable,
            () -> getNavigator().goProvisioningBlocked()
      );
   }

   @Override
   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }

   @Override
   public void retryFetchingCard() {
      getSmartCardInteractor().fetchAssociatedSmartCard().send(new FetchAssociatedSmartCardCommand());
   }

   @Override
   public void cancelFetchingCard() {
      getNavigator().goBack();
   }

   private void onWalletAvailable() {
      getSmartCardInteractor().fetchAssociatedSmartCard()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(getSmartCardInteractor().fetchAssociatedSmartCard()))
            .compose(bindViewIoToMainComposer())
            .subscribe(
                  OperationActionSubscriber.forView(getView().provideOperationView())
                        .onSuccess(command -> handleResult(command.getResult()))
                        .create()
            );
      getSmartCardInteractor().fetchAssociatedSmartCard().send(new FetchAssociatedSmartCardCommand());
   }

   private void handleResult(FetchAssociatedSmartCardCommand.AssociatedCard associatedCard) {
      if (associatedCard.exist()) {
         getNavigator().goCardList();
      } else {
         fetchFirmwareUpdateData();
      }
   }

   private void fetchFirmwareUpdateData() {
      firmwareInteractor.fetchFirmwareUpdateDataPipe()
            .createObservable(new FetchFirmwareUpdateData())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FetchFirmwareUpdateData>()
                  .onSuccess(command -> checkFirmwareUpdateData(command.getResult()))
                  .onFail((command, throwable) -> navigateToWizard())
            );
   }

   private void checkFirmwareUpdateData(FetchFirmwareUpdateData.Result result) {
      if (result.isForceUpdateStarted()) {
         final FirmwareUpdateData firmwareUpdateData = result.firmwareUpdateData();
         //noinspection ConstantConditions
         if (firmwareUpdateData.fileDownloaded()) {
            getNavigator().goInstallFirmware();
         } else {
            getNavigator().goNewFirmwareAvailable();
         }
      } else {
         navigateToWizard();
      }
   }

   private void navigateToWizard() {
      getNavigator().goWizardWelcome(ProvisioningMode.STANDARD);
   }
}
