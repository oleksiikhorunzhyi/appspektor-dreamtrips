package com.worldventures.dreamtrips.wallet.ui.settings.newcard.success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;

import javax.inject.Inject;

import flow.Flow;

public class UnassignSuccessPresenter extends WalletPresenter<UnassignSuccessPresenter.Screen, Parcelable>{

   @Inject Navigator navigator;
   @Inject WizardMemoryStorage wizardMemoryStorage;

   public UnassignSuccessPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   void navigateToWizard() {
      wizardMemoryStorage.setSetupNewCard(true);
      navigator.single(new WizardWelcomePath(), Flow.Direction.REPLACE);
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }
}
