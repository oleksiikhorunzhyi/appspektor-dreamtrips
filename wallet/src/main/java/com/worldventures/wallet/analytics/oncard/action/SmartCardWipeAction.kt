package com.worldventures.wallet.analytics.oncard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker

import io.techery.janet.smartcard.model.analytics.AnalyticsLog

@AnalyticsEvent(action = "wallet:oncard:wipe", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class SmartCardWipeAction(logEntry: AnalyticsLog) : SmartCardAnalyticsAction(logEntry) {

   override fun processLog(type: Int, logEntry: AnalyticsLog) {
      super.processLog(type, logEntry)
      when (type) {
         AnalyticsLog.PAYMENT_CARD_WIPE -> attributeMap.put("ocwipepaycards", "1")
         AnalyticsLog.DEFAULT_CARD_WIPE -> attributeMap.put("ocwipedefaultcard", "1")
      }
   }
}