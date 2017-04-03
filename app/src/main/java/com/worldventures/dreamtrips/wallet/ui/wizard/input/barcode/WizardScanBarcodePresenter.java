package com.worldventures.dreamtrips.wallet.ui.wizard.input.barcode;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.ScidScannedAction;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.SmartCardStatusHandler;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPath;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import timber.log.Timber;

public class WizardScanBarcodePresenter extends WalletPresenter<WizardScanBarcodePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject PermissionDispatcher permissionDispatcher;

   public WizardScanBarcodePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observerAvailabilitySmartCard();
   }

   private void observerAvailabilitySmartCard() {
      wizardInteractor.getSmartCardStatusCommandActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationFetchCardStatus())
                  .onSuccess(command -> SmartCardStatusHandler.handleSmartCardStatus(command.getResult(),
                        statusUnassigned -> cardIsUnassigned(command.getSmartCardId()),
                        statusAssignToAnotherDevice -> Timber.d("This card is assigned to another your device"), //todo: remove this after implement Assign new phone feature.
                        statusAssignedToAnotherUser -> {
                           getView().showErrorCardIsAssignedDialog();
                           getView().restartCamera();
                        }
                  ))
                  .onFail((command, throwable) -> {
                     Timber.e(throwable, "");
                     getView().restartCamera();
                  })
                  .create());
   }

   private void cardIsUnassigned(String smartCardId) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new ScidScannedAction(smartCardId)));
      navigator.go(new PairKeyPath(smartCardId));
   }

   void requestCamera() {
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS)
            .compose(bindView())
            .subscribe(new PermissionSubscriber().onPermissionGrantedAction(() -> getView().startCamera())
                  .onPermissionRationaleAction(() -> getView().showRationaleForCamera())
                  .onPermissionDeniedAction(() -> getView().showDeniedForCamera()));
   }

   void barcodeScanned(String barcode) {
      wizardInteractor.getSmartCardStatusCommandActionPipe().send(new GetSmartCardStatusCommand(barcode));
   }

   void startManualInput() {
      navigator.go(new WizardManualInputPath());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void startCamera();

      void restartCamera();

      void showRationaleForCamera();

      void showDeniedForCamera();

      OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus();

      void showErrorCardIsAssignedDialog();
   }
}
