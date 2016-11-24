package com.worldventures.dreamtrips.wallet.ui.wizard.finish;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class WizardAssignUserPresenter extends WalletPresenter<WizardAssignUserPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public WizardAssignUserPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   interface Screen extends WalletScreen {
   }
}
