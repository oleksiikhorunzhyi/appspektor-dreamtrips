package com.worldventures.wallet.ui.wizard.input.helper;

import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.SmartCardUserCommand;
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.navigation.Navigator;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class InputBarcodeDelegate {

   private final Navigator navigator;
   private final InputAnalyticsDelegate analyticsDelegate;
   private final WizardInteractor wizardInteractor;
   private final SmartCardInteractor smartCardInteractor;

   public InputBarcodeDelegate(
         Navigator navigator,
         WizardInteractor wizardInteractor,
         InputAnalyticsDelegate analyticsDelegate,
         SmartCardInteractor smartCardInteractor) {
      this.navigator = navigator;
      this.analyticsDelegate = analyticsDelegate;
      this.wizardInteractor = wizardInteractor;
      this.smartCardInteractor = smartCardInteractor;
   }

   public void barcodeEntered(String barcode) {
      fetchCardStatus(barcode);
   }

   public void retry(String barcode) {
      fetchCardStatus(barcode);
   }

   private void fetchCardStatus(String barcode) {
      wizardInteractor.getSmartCardStatusCommandActionPipe().send(new GetSmartCardStatusCommand(barcode));
   }

   public void init(InputDelegateView inputDelegateView) {
      wizardInteractor.getSmartCardStatusCommandActionPipe()
            .observe()
            .compose(inputDelegateView.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(inputDelegateView.provideOperationFetchCardStatus())
                  .onSuccess(command -> SmartCardStatusHandler.handleSmartCardStatus(command.getResult(),
                        statusUnassigned -> cardIsUnassigned(command.getSmartCardId()),
                        statusAssignToAnotherDevice -> cardAssignToAnotherDevice(command.getSmartCardId()),
                        statusAssignedToAnotherUser -> inputDelegateView.showErrorCardIsAssignedDialog(),
                        statusAssignedToCurrentDevice -> fetchAssociatedSmartCard()
                  ))
                  .onFail((command, throwable) -> Timber.e(throwable, ""))
                  .create());

      smartCardInteractor.fetchAssociatedSmartCard()
            .observeSuccess()
            .flatMap(cmd -> {
               smartCardInteractor.fetchAssociatedSmartCard().clearReplays();
               return smartCardInteractor.smartCardUserPipe().createObservable(SmartCardUserCommand.fetch());
            })
            .compose(inputDelegateView.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(inputDelegateView.provideOperationFetchSmartCardUser())
                  .onSuccess(command -> handleSmartCardUserExisting(command.getResult()))
                  .create());
   }

   private void fetchAssociatedSmartCard() {
      smartCardInteractor.fetchAssociatedSmartCard().send(new FetchAssociatedSmartCardCommand());
   }

   public void retryAssignedToCurrentDevice() {
      fetchAssociatedSmartCard();
   }

   private void handleSmartCardUserExisting(SmartCardUser smartCardUser) {
      if (smartCardUser != null) {
         navigator.goWizardUploadProfile(ProvisioningMode.STANDARD);
      } else {
         navigator.goWizardEditProfile(ProvisioningMode.STANDARD);
      }
   }

   private void cardAssignToAnotherDevice(String smartCardId) {
      navigator.goExistingDeviceDetected(smartCardId);
   }

   private void cardIsUnassigned(String smartCardId) {
      sendAnalytics(smartCardId);
      navigator.goPairKey(ProvisioningMode.STANDARD, smartCardId);
   }

   private void sendAnalytics(String smartCardId) {
      analyticsDelegate.scannedSuccessfully(smartCardId);
   }
}
