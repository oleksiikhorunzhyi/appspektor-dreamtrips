package com.worldventures.wallet.analytics.firmware.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Ready Checklist", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class UpdateChecksVisitAction : FirmwareAnalyticsAction() {

   @Attribute("scupdatestep3") internal val udateStep3 = "1"
}
