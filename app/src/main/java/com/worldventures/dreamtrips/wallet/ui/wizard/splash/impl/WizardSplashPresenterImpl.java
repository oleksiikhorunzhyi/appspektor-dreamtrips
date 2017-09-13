package com.worldventures.dreamtrips.wallet.ui.wizard.splash.impl;


import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.ScanCardAction;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashScreen;

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
      getView().setup();
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
