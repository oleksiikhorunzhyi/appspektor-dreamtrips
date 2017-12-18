package com.worldventures.wallet.analytics.settings

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:privacy", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class StealthModeAction(stealthModeEnabled: Boolean) : WalletAnalyticsAction() {

   @Attribute("privacystatus") internal val privacyStatus: String = if (stealthModeEnabled) "Private" else "Not Private"
   @Attribute("privacychange") internal var privacyChange = "1"

}
