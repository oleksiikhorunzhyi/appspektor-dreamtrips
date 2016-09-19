package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.barcode.WizardScanBarcodePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPath;

import javax.inject.Inject;

public class WizardSplashPresenter extends WalletPresenter<WizardSplashPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   private boolean termsAccepted;

   public WizardSplashPresenter(Context context, Injector injector, boolean termsAccepted) {
      super(context, injector);
      this.termsAccepted = termsAccepted;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().setup(termsAccepted);
   }

   public void openTerms() {
      navigator.go(new WizardTermsPath());
   }

   public void startScanCard() {
      navigator.go(new WizardScanBarcodePath());
   }

   public void onBack() {
      navigator.goBack();
   }

        public interface Screen extends WalletScreen {

      void setup(boolean termsAccepted);
   }
}
