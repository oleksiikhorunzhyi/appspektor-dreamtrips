package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "wallet:Wallet Home", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class WalletHomeAction(@field:Attribute("numofcards") internal var numOfCards: Int) : WalletAnalyticsAction()
