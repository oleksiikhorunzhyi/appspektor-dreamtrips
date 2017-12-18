package com.worldventures.wallet.analytics.settings

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:auto clear smartcard:changes saved", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class AutoClearChangedAction(@field:Attribute("autoclear") internal val autoClear: String) : WalletAnalyticsAction() {
   @Attribute("autoclearchange") internal val autoClearChange = "1"
}
