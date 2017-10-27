package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 1:Card Successfully Entered", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ScidEnteredAction(cid: String) : WalletAnalyticsAction(cid) {

   @Attribute("cardmaninput") internal val success = "1"
   @Attribute("cardsetupstep2") internal val step = "1"
   @Attribute("cardinputmethod") internal val method = "Manual"
}
