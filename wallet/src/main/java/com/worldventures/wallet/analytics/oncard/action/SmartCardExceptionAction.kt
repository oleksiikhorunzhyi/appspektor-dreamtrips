package com.worldventures.wallet.analytics.oncard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker

import java.util.Locale

import io.techery.janet.smartcard.model.analytics.AnalyticsLog
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryException

@AnalyticsEvent(action = "wallet:oncard:exception", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class SmartCardExceptionAction(logEntry: AnalyticsLog) : SmartCardAnalyticsAction(logEntry) {

   override fun processLog(type: Int, logEntry: AnalyticsLog) {
      super.processLog(type, logEntry)
      when (type) {
         AnalyticsLog.EXCEPTION -> {
            val exception = logEntry as AnalyticsLogEntryException

            attributeMap.put("ocerrcat", exception.exceptionCategory().toString())
            attributeMap.put("ocerrsubcat", exception.exceptionType().toString())
            attributeMap.put("ocerrexcepdetail", exception.errorCode().toString())
            attributeMap.put("ocexception", String.format(Locale.US, "%d-%d-%d",
                  exception.exceptionCategory(),
                  exception.exceptionType(),
                  exception.errorCode()))
         }
      }
   }

}
