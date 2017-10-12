package com.worldventures.dreamtrips.wallet.ui.wizard.input.helper;

import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class InputBarcodeDelegate {

   private final Navigator navigator;
   private final InputAnalyticsDelegate analyticsDelegate;
   private final WizardInteractor wizardInteractor;

   public InputBarcodeDelegate(
         Navigator navigator,
         WizardInteractor wizardInteractor,
         InputAnalyticsDelegate analyticsDelegate) {
      this.navigator = navigator;
      this.analyticsDelegate = analyticsDelegate;
      this.wizardInteractor = wizardInteractor;
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
            .compose(inputDelegateView.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(inputDelegateView.provideOperationFetchCardStatus())
                  .onSuccess(command -> SmartCardStatusHandler.handleSmartCardStatus(command.getResult(),
                        statusUnassigned -> cardIsUnassigned(command.getSmartCardId()),
                        statusAssignToAnotherDevice -> cardAssignToAnotherDevice(command.getSmartCardId()),
                        statusAssignedToAnotherUser -> inputDelegateView.showErrorCardIsAssignedDialog()
                  ))
                  .onFail((command, throwable) -> Timber.e(throwable, ""))
                  .create());
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
