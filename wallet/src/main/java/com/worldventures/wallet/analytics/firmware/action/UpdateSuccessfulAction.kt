package com.worldventures.wallet.analytics.firmware.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Step 5:Update Successful", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class UpdateSuccessfulAction : FirmwareAnalyticsAction() {

   @Attribute("scupdatestep5") internal val updateStep5 = "1"

}
