package com.worldventures.dreamtrips.wallet.analytics.general;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.general.action.SmartCardCommunicationErrorAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardErrorServiceWrapper;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsServiceWrapper;

public class SmartCardAnalyticErrorHandler {

   private final AnalyticsInteractor analyticsInteractor;

   private String currentNavigationState;

   public SmartCardAnalyticErrorHandler(SmartCardErrorServiceWrapper errorServiceWrapper,
         WalletAnalyticsServiceWrapper analyticsServiceWrapper, AnalyticsInteractor analyticsInteractor) {
      this.analyticsInteractor = analyticsInteractor;

      errorServiceWrapper.addRequestFailureListener(this::trackError);
      analyticsServiceWrapper.addNavigationStateListener(state -> currentNavigationState = state);
   }

   private void trackError(Throwable t, String message) {
      if (currentNavigationState != null) {
         analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(
               new SmartCardCommunicationErrorAction(currentNavigationState, message)));
      }
   }
}
