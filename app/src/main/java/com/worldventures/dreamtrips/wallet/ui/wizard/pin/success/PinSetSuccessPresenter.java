package com.worldventures.dreamtrips.wallet.ui.wizard.pin.success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;

import javax.inject.Inject;

public class PinSetSuccessPresenter extends WalletPresenter<PinSetSuccessPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   private final Action mode;

   public PinSetSuccessPresenter(Context context, Injector injector, Action mode) {
      super(context, injector);
      this.mode = mode;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      view.showMode(mode);
   }

   void goToBack() {
      navigator.goBack();
   }

   void goToNext() {
      navigator.go(new WalletSettingsPath());
   }

   public interface Screen extends WalletScreen {

      void showMode(Action mode);
   }
}
