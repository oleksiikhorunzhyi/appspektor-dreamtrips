package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.barcode.WizardScanBarcodePath;

import javax.inject.Inject;

import flow.Flow;

public class WizardSplashScreenPresenter extends WalletPresenter<WizardSplashScreenPresenter.Screen, Parcelable> {

   @Inject Activity activity;

   public WizardSplashScreenPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public void startScanCard() {
      Flow.get(getContext()).set(new WizardScanBarcodePath());
   }

   public interface Screen extends WalletScreen {}

   public void onBack() {
      // Flow.goBack() in this case is useless, because flow stack is empty (goBack modifies only stack).
      activity.onBackPressed();
   }
}
