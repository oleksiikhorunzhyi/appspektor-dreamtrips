package com.worldventures.wallet.analytics.settings

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:settings:disable default payment after:changes saved", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class DisableDefaultChangedAction(@field:Attribute("disabledefault") internal val disableDefault: String) : WalletAnalyticsAction() {
   @Attribute("disabledefaultchange") internal val disableDefaultChange = "1"
}
