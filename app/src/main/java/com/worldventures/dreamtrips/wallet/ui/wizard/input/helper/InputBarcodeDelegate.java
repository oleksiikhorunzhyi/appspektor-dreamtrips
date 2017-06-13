package com.worldventures.dreamtrips.wallet.ui.wizard.input.helper;

import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.ExistingDeviceDetectPath;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class InputBarcodeDelegate {

   private final Navigator navigator;
   private final InputAnalyticsDelegate analyticsDelegate;
   private final WizardInteractor wizardInteractor;
   private final InputDelegateView inputDelegateView;

   public InputBarcodeDelegate(
         Navigator navigator,
         WizardInteractor wizardInteractor,
         InputDelegateView inputDelegateView,
         InputAnalyticsDelegate analyticsDelegate) {
      this.navigator = navigator;
      this.analyticsDelegate = analyticsDelegate;
      this.wizardInteractor = wizardInteractor;
      this.inputDelegateView = inputDelegateView;

      init();
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

   private void init() {
      wizardInteractor.getSmartCardStatusCommandActionPipe()
            .observe()
            .compose(RxLifecycle.bindView(inputDelegateView.getView()))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(inputDelegateView.provideOperationFetchCardStatus())
                  .onSuccess(command -> SmartCardStatusHandler.handleSmartCardStatus(command.getResult(),
                        statusUnassigned -> cardIsUnassigned(command.getSmartCardId()),
                        statusAssignToAnotherDevice -> cardAssignToAnotherDevice(command.getSmartCardId()),
                        statusAssignedToAnotherUser -> {
                           inputDelegateView.showErrorCardIsAssignedDialog();
                           inputDelegateView.reset();
                        }
                  ))
                  .onFail((command, throwable) -> {
                     Timber.e(throwable, "");
                     inputDelegateView.reset();
                  })
                  .create());
   }

   private void cardAssignToAnotherDevice(String smartCardId) {
      navigator.go(new ExistingDeviceDetectPath(smartCardId));
   }

   private void cardIsUnassigned(String smartCardId) {
      sendAnalytics(smartCardId);
      navigator.go(new PairKeyPath(ProvisioningMode.STANDARD, smartCardId));
   }

   private void sendAnalytics(String smartCardId) {
      analyticsDelegate.scannedSuccessfully(smartCardId);
   }
}
