package com.worldventures.wallet.analytics.locatecard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker

@AnalyticsEvent(action = "wallet:settings:locate smartcard:disable tracking", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class LocateDisabledAnalyticsAction : BaseLocateSmartCardAction() {

   @Attribute("trackingenabled") internal var trackingEnabled = "No"
   @Attribute("disabletracking ") internal var disableTracking = "1"
}
