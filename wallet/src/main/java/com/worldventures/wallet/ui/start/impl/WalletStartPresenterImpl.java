package com.worldventures.wallet.ui.start.impl;

import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAccessValidator;
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.wallet.service.firmware.command.FetchFirmwareUpdateData;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.start.WalletStartPresenter;
import com.worldventures.wallet.ui.start.WalletStartScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WalletStartPresenterImpl extends WalletPresenterImpl<WalletStartScreen> implements WalletStartPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final FirmwareInteractor firmwareInteractor;
   private final WalletAccessValidator walletAccessValidator;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;


   public WalletStartPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor, WalletAccessValidator walletAccessValidator,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
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
      smartCardInteractor.fetchAssociatedSmartCard().send(new FetchAssociatedSmartCardCommand());
   }

   @Override
   public void cancelFetchingCard() {
      getNavigator().goBack();
   }

   private void onWalletAvailable() {
      smartCardInteractor.fetchAssociatedSmartCard()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.fetchAssociatedSmartCard()))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                  OperationActionSubscriber.forView(getView().provideOperationView())
                        .onSuccess(command -> handleResult(command.getResult()))
                        .create()
            );
      smartCardInteractor.fetchAssociatedSmartCard().send(new FetchAssociatedSmartCardCommand());
   }

   private void handleResult(FetchAssociatedSmartCardCommand.AssociatedCard associatedCard) {
      if (associatedCard.getExist()) {
         getNavigator().goCardList();
      } else {
         fetchFirmwareUpdateData();
      }
   }

   private void fetchFirmwareUpdateData() {
      firmwareInteractor.fetchFirmwareUpdateDataPipe()
            .createObservable(new FetchFirmwareUpdateData())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<FetchFirmwareUpdateData>()
                  .onSuccess(command -> checkFirmwareUpdateData(command.getResult()))
                  .onFail((command, throwable) -> navigateToWizard())
            );
   }

   private void checkFirmwareUpdateData(FetchFirmwareUpdateData.Result result) {
      if (result.isForceUpdateStarted()) {
         final FirmwareUpdateData firmwareUpdateData = result.getFirmwareUpdateData();
         //noinspection ConstantConditions
         if (firmwareUpdateData.isFileDownloaded()) {
            getNavigator().goInstallFirmwareWalletStart();
         } else {
            getNavigator().goNewFirmwareAvailableWalletStart();
         }
      } else {
         navigateToWizard();
      }
   }

   private void navigateToWizard() {
      getNavigator().goWizardWelcomeWalletStart(ProvisioningMode.STANDARD);
   }
}
