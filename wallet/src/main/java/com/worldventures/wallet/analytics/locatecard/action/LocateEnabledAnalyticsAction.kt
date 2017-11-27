package com.worldventures.wallet.analytics.locatecard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker

@AnalyticsEvent(action = "wallet:settings:locate smartcard:enable tracking", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class LocateEnabledAnalyticsAction : BaseLocateSmartCardAction() {

   @Attribute("trackingenabled") internal var trackingEnabled = "Yes"
   @Attribute("enabletracking") internal var enableTracking = "1"
}
