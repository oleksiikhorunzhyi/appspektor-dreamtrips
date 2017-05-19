package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.wizard.ScanCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.barcode.WizardScanBarcodePath;

import javax.inject.Inject;

public class WizardSplashPresenter extends WalletPresenter<WizardSplashPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;

   public WizardSplashPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      getView().setup();
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new ScanCardAction()));
   }

   public void startScanCard() {
      navigator.go(new WizardScanBarcodePath());
   }

   public void onBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void setup();
   }
}
