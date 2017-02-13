package com.worldventures.dreamtrips.wallet.ui.settings.newcard.success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class UnassignSuccessPresenter extends WalletPresenter<UnassignSuccessPresenter.Screen, Parcelable>{

   @Inject Navigator navigator;

   public UnassignSuccessPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   public void goNext() {

   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

   }
}
