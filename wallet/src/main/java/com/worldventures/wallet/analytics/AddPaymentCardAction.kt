package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "wallet:Add a Card:Add Payment Card", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class AddPaymentCardAction : WalletAnalyticsAction() {

   @Attribute("addcard") internal val addCard = "1"
   @Attribute("cardtype") internal val cardType = "Payment"
}
