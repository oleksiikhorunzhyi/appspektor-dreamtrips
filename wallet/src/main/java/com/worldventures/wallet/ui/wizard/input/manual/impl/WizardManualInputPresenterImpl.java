package com.worldventures.wallet.ui.wizard.input.manual.impl;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.wizard.ManualCardInputAction;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.input.helper.InputBarcodeDelegate;
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputPresenter;
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputScreen;

public class WizardManualInputPresenterImpl extends WalletPresenterImpl<WizardManualInputScreen> implements WizardManualInputPresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;
   private final InputBarcodeDelegate inputBarcodeDelegate;

   public WizardManualInputPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, InputBarcodeDelegate inputBarcodeDelegate) {
      super(navigator, deviceConnectionDelegate);
      this.analyticsInteractor = analyticsInteractor;
      this.inputBarcodeDelegate = inputBarcodeDelegate;
   }

   @Override
   public void attachView(WizardManualInputScreen view) {
      super.attachView(view);
      inputBarcodeDelegate.init(view);
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new ManualCardInputAction()));
      observeScidInput();
   }

   private void observeScidInput() {
      //noinspection ConstantConditions
      getView().scidInput()
            .compose(getView().bindUntilDetach())
            .subscribe(scid -> getView().buttonEnable(scid.length() == getView().getScIdLength()));
   }

   @Override
   public void checkBarcode(String barcode) {
      inputBarcodeDelegate.barcodeEntered(barcode);
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
   public void retryAssignedToCurrentDevice() {
      inputBarcodeDelegate.retryAssignedToCurrentDevice();
   }
}
