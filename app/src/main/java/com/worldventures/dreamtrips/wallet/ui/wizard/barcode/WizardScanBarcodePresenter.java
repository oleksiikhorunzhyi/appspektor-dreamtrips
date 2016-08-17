package com.worldventures.dreamtrips.wallet.ui.wizard.barcode;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.WizardCodeHelper;
import com.worldventures.dreamtrips.wallet.ui.wizard.manual.WizardManualInputPath;

import javax.inject.Inject;

import flow.Flow;

public class WizardScanBarcodePresenter extends WalletPresenter<WizardScanBarcodePresenter.Screen, Parcelable> {
   @Inject WizardCodeHelper wizardCodeHelper;

   @Inject PermissionDispatcher permissionDispatcher;

   public WizardScanBarcodePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public void requestCamera() {
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS)
            .compose(bindView())
            .subscribe(new PermissionSubscriber().onPermissionGrantedAction(() -> getView().startCamera())
                  .onPermissionRationaleAction(() -> getView().showRationaleForCamera())
                  .onPermissionDeniedAction(() -> getView().showDeniedForCamera()));
   }

   public void barcodeScanned(String barcode) {
      wizardCodeHelper.createAndConnect(getView().provideOperationDelegate(), barcode, bindViewIoToMainComposer());
   }

   public void startManualInput() {
      Flow.get(getContext()).set(new WizardManualInputPath());
   }


   public void goBack() {
      Flow.get(getContext()).goBack();
   }

   public interface Screen extends WalletScreen {
      void startCamera();

      void showRationaleForCamera();

      void showDeniedForCamera();
   }
}