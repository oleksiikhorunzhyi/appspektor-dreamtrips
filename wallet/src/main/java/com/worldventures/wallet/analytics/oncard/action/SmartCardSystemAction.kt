package com.worldventures.wallet.analytics.oncard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker

import io.techery.janet.smartcard.model.analytics.AnalyticsLog
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryRestart

@AnalyticsEvent(action = "wallet:oncard:system", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class SmartCardSystemAction(logEntry: AnalyticsLog) : SmartCardAnalyticsAction(logEntry) {

   override fun processLog(type: Int, logEntry: AnalyticsLog) {
      super.processLog(type, logEntry)
      when (type) {
         AnalyticsLog.RESTART -> {
            val restart = logEntry as AnalyticsLogEntryRestart
            attributeMap.put("ocrestart", "1")
            attributeMap.put("ocrestartcode", restart.reason().toString())
         }
         AnalyticsLog.SET_TIME -> attributeMap.put("ocsettime", "1")
      }
   }

}
