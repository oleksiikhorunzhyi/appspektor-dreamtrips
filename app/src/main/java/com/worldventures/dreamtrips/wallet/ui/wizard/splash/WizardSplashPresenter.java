package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.barcode.WizardScanBarcodePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPath;

import javax.inject.Inject;

import flow.Flow;

public class WizardSplashPresenter extends WalletPresenter<WizardSplashPresenter.Screen, Parcelable> {

   @Inject Activity activity;

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
      Flow.get(getContext()).set(new WizardTermsPath());
   }

   public void startScanCard() {
      Flow.get(getContext()).set(new WizardScanBarcodePath());
   }

   public void onBack() {
      // Flow.goBack() in this case is useless, because flow stack is empty (goBack modifies only stack).
      activity.finish();
   }

        public interface Screen extends WalletScreen {

      void setup(boolean termsAccepted);
   }
}
