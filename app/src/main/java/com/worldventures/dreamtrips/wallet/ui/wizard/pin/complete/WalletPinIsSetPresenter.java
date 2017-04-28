package com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PinWasSetAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WizardAssignUserPath;

import javax.inject.Inject;

public class WalletPinIsSetPresenter extends WalletPresenter<WalletPinIsSetPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SmartCardInteractor smartCardInteractor;

   public WalletPinIsSetPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new PinWasSetAction()));
   }

   public void goBack() {
      navigator.goBack();
   }

   void navigateToNextScreen() {
      navigator.go(new WizardAssignUserPath());
   }

   public interface Screen extends WalletScreen {

   }
}
