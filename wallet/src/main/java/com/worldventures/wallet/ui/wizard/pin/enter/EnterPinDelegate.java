package com.worldventures.wallet.ui.wizard.pin.enter;

import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.settings.ResetPinAction;
import com.worldventures.wallet.analytics.settings.ResetPinSuccessAction;
import com.worldventures.wallet.analytics.wizard.SetPinAction;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.pin.Action;

public abstract class EnterPinDelegate {

   private final WalletAnalyticsInteractor analyticsInteractor;
   private final Navigator navigator;

   private EnterPinDelegate(WalletAnalyticsInteractor analyticsInteractor, Navigator navigator) {
      this.analyticsInteractor = analyticsInteractor;
      this.navigator = navigator;
   }

   public static EnterPinDelegate create(Action action, WalletAnalyticsInteractor analyticsInteractor, Navigator navigator) {
      if (action == Action.ADD) {
         return new AddPinDelegate(analyticsInteractor, navigator);
      } else if (action == Action.RESET) {
         return new ResetPinDelegate(analyticsInteractor, navigator);
      } else {
         return new SetupPinDelegate(analyticsInteractor, navigator);
      }
   }

   public final void trackScreen() {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(trackScreenAnalyticsAction()));
   }

   public WalletAnalyticsInteractor getAnalyticsInteractor() {
      return analyticsInteractor;
   }

   public Navigator getNavigator() {
      return navigator;
   }

   protected abstract WalletAnalyticsAction trackScreenAnalyticsAction();

   public abstract void pinEntered();

   public abstract void prepareView(PinView view);

   private final static class AddPinDelegate extends EnterPinDelegate {

      private AddPinDelegate(WalletAnalyticsInteractor analyticsInteractor, Navigator navigator) {
         super(analyticsInteractor, navigator);
      }

      @Override
      protected WalletAnalyticsAction trackScreenAnalyticsAction() {
         //// TODO: 4/13/17 should be updated
         return new ResetPinAction();
      }

      @Override
      public void pinEntered() {
         getNavigator().goPinSetSuccess(Action.ADD);
      }

      @Override
      public void prepareView(PinView view) {
         view.addMode();
      }
   }

   private final static class ResetPinDelegate extends EnterPinDelegate {

      private ResetPinDelegate(WalletAnalyticsInteractor analyticsInteractor, Navigator navigator) {
         super(analyticsInteractor, navigator);
      }

      @Override
      protected WalletAnalyticsAction trackScreenAnalyticsAction() {
         return new ResetPinAction();
      }

      @Override
      public void pinEntered() {
         getAnalyticsInteractor().walletAnalyticsPipe()
               .send(new WalletAnalyticsCommand(new ResetPinSuccessAction()));

         getNavigator().goPinSetSuccess(Action.ADD);
      }

      @Override
      public void prepareView(PinView view) {
         view.resetMode();
      }
   }

   private final static class SetupPinDelegate extends EnterPinDelegate {

      private SetupPinDelegate(WalletAnalyticsInteractor analyticsInteractor, Navigator navigator) {
         super(analyticsInteractor, navigator);
      }

      @Override
      protected WalletAnalyticsAction trackScreenAnalyticsAction() {
         return new SetPinAction();
      }

      @Override
      public void pinEntered() {
         getNavigator().goWalletPinIsSet();
      }

      @Override
      public void prepareView(PinView view) {
         view.setupMode();
      }
   }

   interface PinView {
      void addMode();

      void setupMode();

      void resetMode();
   }
}
