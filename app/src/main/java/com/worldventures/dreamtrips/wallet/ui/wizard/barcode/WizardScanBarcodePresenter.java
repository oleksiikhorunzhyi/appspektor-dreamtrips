package com.worldventures.dreamtrips.wallet.ui.wizard.barcode;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.ScidScannedAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.AvailabilitySmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.manual.WizardManualInputPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPath;

import javax.inject.Inject;

public class WizardScanBarcodePresenter extends WalletPresenter<WizardScanBarcodePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject PermissionDispatcher permissionDispatcher;
   @Inject WizardMemoryStorage wizardMemoryStorage;

   public WizardScanBarcodePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observerAvailabilitySmartCard();
   }

   private void observerAvailabilitySmartCard() {
      wizardInteractor.availabilitySmartCardCommandActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<AvailabilitySmartCardCommand>forView(getView().provideOperationDelegate())
                  .onStart(getContext().getString(R.string.wallet_wizard_assigning_msg))
                  .onSuccess(command -> {
                     analyticsInteractor.walletAnalyticsCommandPipe()
                           .send(new WalletAnalyticsCommand(new ScidScannedAction(command.getSmartCardId())));
                     navigator.go(new PairKeyPath());
                  })
                  .onFail(ErrorHandler.<AvailabilitySmartCardCommand>builder(getContext())
                        .defaultAction(command -> getView().restartCamera())
                        .build())
                  .wrap());
   }

   public void requestCamera() {
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS)
            .compose(bindView())
            .subscribe(new PermissionSubscriber().onPermissionGrantedAction(() -> getView().startCamera())
                  .onPermissionRationaleAction(() -> getView().showRationaleForCamera())
                  .onPermissionDeniedAction(() -> getView().showDeniedForCamera()));
   }

   public void barcodeScanned(String barcode) {
      wizardMemoryStorage.saveBarcode(barcode);
      wizardInteractor.availabilitySmartCardCommandActionPipe().send(new AvailabilitySmartCardCommand(barcode));
   }

   public void startManualInput() {
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
   }
}
