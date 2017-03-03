package com.worldventures.dreamtrips.wallet.ui.settings.newcard.success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.UnAssignCardSuccessAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.UnAssignCardSuccessGetStartedAction;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;

import javax.inject.Inject;

import flow.Flow;

public class UnassignSuccessPresenter extends WalletPresenter<UnassignSuccessPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;

   public UnassignSuccessPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      sendAnalyticAction(new UnAssignCardSuccessAction());
   }

   void navigateToWizard() {
      sendAnalyticAction(new UnAssignCardSuccessGetStartedAction());
      navigator.single(new WizardWelcomePath(), Flow.Direction.REPLACE);
   }

   public void goBack() {
      navigator.goBack();
   }

   private void sendAnalyticAction(WalletAnalyticsAction action) {
      analyticsInteractor
            .walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(action));
   }

   public interface Screen extends WalletScreen {
   }
}