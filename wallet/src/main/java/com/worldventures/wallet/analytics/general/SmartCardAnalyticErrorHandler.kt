package com.worldventures.wallet.analytics.general

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.general.action.SmartCardCommunicationErrorAction
import com.worldventures.wallet.service.SmartCardErrorServiceWrapper
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletAnalyticsServiceWrapper

class SmartCardAnalyticErrorHandler(errorServiceWrapper: SmartCardErrorServiceWrapper,
                                    analyticsServiceWrapper: WalletAnalyticsServiceWrapper, private val analyticsInteractor: WalletAnalyticsInteractor) {

   private var currentNavigationState: String? = null

   init {
      errorServiceWrapper.addRequestFailureListener { _, message -> this.trackError(message) }
      analyticsServiceWrapper.addNavigationStateListener { state -> currentNavigationState = state }
   }

   private fun trackError(message: String) {
      val state = currentNavigationState
      if (state != null) {
         analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(
               SmartCardCommunicationErrorAction(state, message)))
      }
   }
}
