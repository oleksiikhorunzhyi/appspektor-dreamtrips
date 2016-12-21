package com.worldventures.dreamtrips.wallet.ui.settings.about;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class AboutPresenter extends WalletPresenter<AboutPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   public AboutPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }

}
