package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.wallet.domain.entity.record.Record

@AnalyticsEvent(action = "wallet:Add Default Card:Set as Default Card", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SetDefaultCardAction(record: Record) : BaseSetDefaultCardAction() {

   init {
      setDefaultWhere = "In-App:Wallet Home"
      fillRecordDetails(record)
   }
}
