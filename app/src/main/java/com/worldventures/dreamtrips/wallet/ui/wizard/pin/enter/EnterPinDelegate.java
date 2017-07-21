package com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.ResetPinAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.ResetPinSuccessAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.SetPinAction;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;

public abstract class EnterPinDelegate {

   private final AnalyticsInteractor analyticsInteractor;
   private final NavigatorConductor navigator;

   private EnterPinDelegate(AnalyticsInteractor analyticsInteractor, NavigatorConductor navigator) {
      this.analyticsInteractor = analyticsInteractor;
      this.navigator = navigator;
   }

   public static EnterPinDelegate create(Action action, AnalyticsInteractor analyticsInteractor, NavigatorConductor navigator) {
      if (action == Action.ADD) {
         return new AddPinDelegate(analyticsInteractor, navigator);
      } else if (action == Action.RESET) {
         return new ResetPinDelegate(analyticsInteractor, navigator);
      } else {
         return new SetupPinDelegate(analyticsInteractor, navigator);
      }
   }

   public final void trackScreen() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(trackScreenAnalyticsAction()));
   }

   public AnalyticsInteractor getAnalyticsInteractor() {
      return analyticsInteractor;
   }

   public NavigatorConductor getNavigator() {
      return navigator;
   }

   protected abstract WalletAnalyticsAction trackScreenAnalyticsAction();

   public abstract void pinEntered();

   public abstract void prepareView(PinView view);

   private static class AddPinDelegate extends EnterPinDelegate {

      private AddPinDelegate(AnalyticsInteractor analyticsInteractor, NavigatorConductor navigator) {
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

   private static class ResetPinDelegate extends EnterPinDelegate {

      private ResetPinDelegate(AnalyticsInteractor analyticsInteractor, NavigatorConductor navigator) {
         super(analyticsInteractor, navigator);
      }

      @Override
      protected WalletAnalyticsAction trackScreenAnalyticsAction() {
         return new ResetPinAction();
      }

      @Override
      public void pinEntered() {
         getAnalyticsInteractor().walletAnalyticsCommandPipe()
               .send(new WalletAnalyticsCommand(new ResetPinSuccessAction()));

         getNavigator().goPinSetSuccess(Action.ADD);
      }

      @Override
      public void prepareView(PinView view) {
         view.resetMode();
      }
   }

   private static class SetupPinDelegate extends EnterPinDelegate {

      private SetupPinDelegate(AnalyticsInteractor analyticsInteractor, NavigatorConductor navigator) {
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
