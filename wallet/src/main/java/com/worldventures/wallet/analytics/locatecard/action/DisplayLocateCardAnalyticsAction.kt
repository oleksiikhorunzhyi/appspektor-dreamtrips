package com.worldventures.wallet.analytics.locatecard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker

@AnalyticsEvent(action = "wallet:settings:locate smartcard", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class DisplayLocateCardAnalyticsAction : BaseLocateSmartCardAction() {

   @Attribute("trackingenabled") internal val trackingEnabled = "No"
}
