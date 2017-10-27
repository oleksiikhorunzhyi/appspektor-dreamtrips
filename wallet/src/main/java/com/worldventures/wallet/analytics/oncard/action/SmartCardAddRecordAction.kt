package com.worldventures.wallet.analytics.oncard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker

import io.techery.janet.smartcard.model.analytics.AnalyticsLog

@AnalyticsEvent(action = "wallet:oncard:card add", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class SmartCardAddRecordAction(logEntry: AnalyticsLog) : SmartCardAnalyticsAction(logEntry) {

   override fun processLog(type: Int, logEntry: AnalyticsLog) {
      super.processLog(type, logEntry)
      when (type) {
         AnalyticsLog.CARD_READ_PASS -> attributeMap.put("ocsuccardswipe", "1")
         AnalyticsLog.CARD_READ_ERROR -> attributeMap.put("ocfailcardswipe", "1")
         AnalyticsLog.CARD_READ_FORMAT_ERROR -> attributeMap.put("ocformaterrorswipe", "1")
         AnalyticsLog.CARD_READ_NAME_ERROR -> attributeMap.put("ocnameerrorswipe", "1")
      }
   }
}