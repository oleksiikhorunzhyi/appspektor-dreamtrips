package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:Power on your flye SmartCard", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class PowerOnAction : WalletAnalyticsAction()
