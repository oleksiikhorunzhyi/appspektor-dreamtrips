package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.wallet.domain.entity.record.Record

@AnalyticsEvent(action = "wallet:Card Detail:Set as Default Card", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ChangeDefaultCardAction(record: Record) : BaseSetDefaultCardAction() {

   @Attribute("paycardnickname") internal var cardNickname: String

   init {
      setDefaultWhere = "In-App:Card Detail"
      fillRecordDetails(record)
      cardNickname = record.nickname
   }
}
