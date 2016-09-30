package com.worldventures.dreamtrips.wallet.ui.wizard.pin_set_success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletSettingsPath;

import javax.inject.Inject;


public class PinSetSuccessPresenter extends WalletPresenter<PinSetSuccessPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public PinSetSuccessPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public void goToBack() {
      navigator.goBack();
   }

   public void goToNext() {
      navigator.go(new WalletSettingsPath());
   }

   public interface Screen extends WalletScreen {
   }
}
