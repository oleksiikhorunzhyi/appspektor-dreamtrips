package com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.impl;

import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.ManualCardInputAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputBarcodeDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputScreen;

public class WizardManualInputPresenterImpl extends WalletPresenterImpl<WizardManualInputScreen> implements WizardManualInputPresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;
   private final InputBarcodeDelegate inputBarcodeDelegate;

   public WizardManualInputPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, WalletAnalyticsInteractor analyticsInteractor, InputBarcodeDelegate inputBarcodeDelegate) {
      super(navigator, smartCardInteractor, networkService);
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
            .compose(bindView())
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
}
