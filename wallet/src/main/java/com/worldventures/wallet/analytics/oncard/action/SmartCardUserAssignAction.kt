package com.worldventures.wallet.analytics.oncard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker

import io.techery.janet.smartcard.model.analytics.AnalyticsLog

@AnalyticsEvent(action = "wallet:oncard:user assigned", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class SmartCardUserAssignAction(logEntry: AnalyticsLog) : SmartCardUserAction(logEntry) {

   override fun setUserId(userId: Int) {
      if (userId != 0) attributeMap.put("ocmemassigned", userId.toString())
   }
}