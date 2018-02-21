package com.worldventures.wallet.ui.wizard.splash.impl;


import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.wizard.ScanCardAction;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.wallet.ui.wizard.splash.WizardSplashScreen;

public class WizardSplashPresenterImpl extends WalletPresenterImpl<WizardSplashScreen> implements WizardSplashPresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;

   public WizardSplashPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WizardSplashScreen view) {
      super.attachView(view);
      trackScreen();
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new ScanCardAction()));
   }

   public void startScanCard() {
      getNavigator().goWizardScanBarcode();
   }

   public void onBack() {
      getNavigator().goBack();
   }
}
