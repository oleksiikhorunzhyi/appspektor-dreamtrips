package com.worldventures.dreamtrips.wallet.ui.wizard.barcode;

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
import com.worldventures.dreamtrips.wallet.ui.wizard.associate.ConnectSmartCardPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.manual.WizardManualInputPath;

import javax.inject.Inject;

public class WizardScanBarcodePresenter extends WalletPresenter<WizardScanBarcodePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
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
      navigator.go(new ConnectSmartCardPath(ConnectSmartCardPath.BarcodeOrigin.SCAN, barcode));
   }

   public void startManualInput() {
      navigator.go(new WizardManualInputPath());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void startCamera();

      void showRationaleForCamera();

      void showDeniedForCamera();
   }
}
