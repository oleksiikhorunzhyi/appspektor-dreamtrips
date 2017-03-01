package com.worldventures.dreamtrips.wallet.ui.settings.factory_reset_success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.settings.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class FactoryResetSuccessPresenter extends WalletPresenter<FactoryResetSuccessPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;

   public FactoryResetSuccessPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      trackScreen();
   }

   private void trackScreen() {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new FactoryResetAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   public void goToNext() {
         navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }
}
