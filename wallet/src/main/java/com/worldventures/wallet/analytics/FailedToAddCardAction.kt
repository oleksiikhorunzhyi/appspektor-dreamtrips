package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "wallet:Add a Card:Error Adding", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class FailedToAddCardAction private constructor(@field:Attribute("adderrortype") internal val errorType: String) : WalletAnalyticsAction() {

   @Attribute("addcarderror") internal val addCardError = "1"
   @Attribute("cardtype") internal val cardType = "Payment"

   companion object {

      fun noCardConnection(): FailedToAddCardAction {
         return FailedToAddCardAction("No Card Connection")
      }

      fun noNetworkConnection(): FailedToAddCardAction {
         return FailedToAddCardAction("No Network Connection")
      }
   }

}
