package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.impl;


import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputAnalyticsDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputBarcodeDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScanBarcodeScreen;

public class WizardScanBarcodePresenterImpl extends WalletPresenterImpl<WizardScanBarcodeScreen> implements WizardScanBarcodePresenter {

   private final PermissionDispatcher permissionDispatcher;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;

   private final InputBarcodeDelegate inputBarcodeDelegate;

   public WizardScanBarcodePresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, WizardInteractor wizardInteractor, AnalyticsInteractor analyticsInteractor,
         PermissionDispatcher permissionDispatcher, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, smartCardInteractor, networkService);
      this.permissionDispatcher = permissionDispatcher;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
      this.inputBarcodeDelegate = new InputBarcodeDelegate(navigator, wizardInteractor,
            InputAnalyticsDelegate.createForScannerScreen(analyticsInteractor));
   }

   @Override
   public void attachView(WizardScanBarcodeScreen view) {
      super.attachView(view);
      inputBarcodeDelegate.init(view);
   }

   @Override
   public void requestCamera() {
      //noinspection ConstantConditions
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS)
            .compose(bindView())
            .subscribe(new PermissionSubscriber().onPermissionGrantedAction(() -> getView().startCamera())
                  .onPermissionRationaleAction(() -> getView().showRationaleForCamera())
                  .onPermissionDeniedAction(() -> getView().showDeniedForCamera()));
   }

   @Override
   public void barcodeScanned(String barcode) {
      inputBarcodeDelegate.barcodeEntered(barcode);
   }

   @Override
   public void startManualInput() {
      getNavigator().goWizardManualInput();
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void retry(String barcode) {
      inputBarcodeDelegate.retry(barcode);
   }

   @Override
   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }
}
