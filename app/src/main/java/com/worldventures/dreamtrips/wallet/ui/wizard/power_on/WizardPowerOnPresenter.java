package com.worldventures.dreamtrips.wallet.ui.wizard.power_on;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.checking.WizardCheckingPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPath;

import javax.inject.Inject;

public class WizardPowerOnPresenter extends WalletPresenter<WizardPowerOnPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public WizardPowerOnPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public void openWelcome() {
      navigator.single(new WizardCheckingPath());
   }

   public void onBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }
}
