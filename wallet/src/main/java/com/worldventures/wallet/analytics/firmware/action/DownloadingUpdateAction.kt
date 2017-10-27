package com.worldventures.wallet.analytics.firmware.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 2",
      navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class DownloadingUpdateAction : FirmwareAnalyticsAction() {

   @Attribute("scupdatestep2") internal val updateStep2 = "1"
}
