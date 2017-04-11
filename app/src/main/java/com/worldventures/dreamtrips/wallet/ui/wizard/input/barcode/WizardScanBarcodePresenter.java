package com.worldventures.dreamtrips.wallet.ui.wizard.input.barcode;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputAnalyticsDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputBarcodeDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputDelegateView;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputPath;

import javax.inject.Inject;

public class WizardScanBarcodePresenter extends WalletPresenter<WizardScanBarcodePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject PermissionDispatcher permissionDispatcher;

   private InputBarcodeDelegate inputBarcodeDelegate;

   public WizardScanBarcodePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      // analytics from this screen is sent from WizardSplashPresenter

      inputBarcodeDelegate = new InputBarcodeDelegate(navigator, wizardInteractor,
            getView(), InputAnalyticsDelegate.createForScannerScreen(analyticsInteractor));
   }

   void requestCamera() {
      //noinspection ConstantConditions
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS)
            .compose(bindView())
            .subscribe(new PermissionSubscriber().onPermissionGrantedAction(() -> getView().startCamera())
                  .onPermissionRationaleAction(() -> getView().showRationaleForCamera())
                  .onPermissionDeniedAction(() -> getView().showDeniedForCamera()));
   }

   void barcodeScanned(String barcode) {
      inputBarcodeDelegate.barcodeEntered(barcode);
   }

   void startManualInput() {
      navigator.go(new WizardManualInputPath());
   }

   void goBack() {
      navigator.goBack();
   }

   void retry(String barcode) {
      inputBarcodeDelegate.retry(barcode);
   }

   public interface Screen extends WalletScreen, InputDelegateView {

      void startCamera();

      void showRationaleForCamera();

      void showDeniedForCamera();
   }
}
