package com.worldventures.dreamtrips.wallet.ui.settings.factory_reset_success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class FactoryResetSuccessPresenter extends WalletPresenter<FactoryResetSuccessPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public FactoryResetSuccessPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public void goToNext() {
         navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }
}
