package com.worldventures.wallet.analytics.oncard.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.domain.entity.record.Record

import io.techery.janet.smartcard.model.analytics.AnalyticsLog
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryCardSwipe
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryPaymentMode

@AnalyticsEvent(action = "wallet:oncard:pay", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SmartCardPaymentAction internal constructor(logEntry: AnalyticsLog) : SmartCardAnalyticsAction(logEntry) {

   var recordId: Int = 0
      private set

   fun setRecord(record: Record?) {
      if (record != null) {
         attributeMap.put("paycardnickname", record.nickName())
         fillRecordDetails(record)
      }
   }

   override fun processLog(type: Int, logEntry: AnalyticsLog) {
      super.processLog(type, logEntry)
      when (type) {
         AnalyticsLog.PAYMENT_MODE -> {
            val paymentMode = logEntry as AnalyticsLogEntryPaymentMode
            recordId = paymentMode.cardId()

            attributeMap.put("paymentMode", "1")
         }
         AnalyticsLog.CARD_SWIPE -> {
            val cardSwipe = logEntry as AnalyticsLogEntryCardSwipe
            recordId = cardSwipe.cardId()

            attributeMap.put("ocpayswipe", cardSwipe.swipeSequenceNumber().toString())
            attributeMap.put("ocpayswipespd", cardSwipe.swipeSpeed().toString())
            attributeMap.put("ocpaytrack", cardSwipe.trackIdentifier().toString())
         }
      }
   }
}
