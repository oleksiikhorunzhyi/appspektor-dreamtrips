package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "wallet:Card Detail", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class CardDetailsAction(@field:Attribute("paycardnickname") internal val cardNickname: String) : BaseCardDetailsWithDefaultAction()
