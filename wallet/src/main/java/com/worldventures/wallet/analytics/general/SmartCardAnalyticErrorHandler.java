package com.worldventures.wallet.analytics.general;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.general.action.SmartCardCommunicationErrorAction;
import com.worldventures.wallet.service.SmartCardErrorServiceWrapper;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletAnalyticsServiceWrapper;

public class SmartCardAnalyticErrorHandler {

   private final WalletAnalyticsInteractor analyticsInteractor;

   private String currentNavigationState;

   public SmartCardAnalyticErrorHandler(SmartCardErrorServiceWrapper errorServiceWrapper,
         WalletAnalyticsServiceWrapper analyticsServiceWrapper, WalletAnalyticsInteractor analyticsInteractor) {
      this.analyticsInteractor = analyticsInteractor;

      errorServiceWrapper.addRequestFailureListener(this::trackError);
      analyticsServiceWrapper.addNavigationStateListener(state -> currentNavigationState = state);
   }

   private void trackError(Throwable t, String message) {
      if (currentNavigationState != null) {
         analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(
               new SmartCardCommunicationErrorAction(currentNavigationState, message)));
      }
   }
}
