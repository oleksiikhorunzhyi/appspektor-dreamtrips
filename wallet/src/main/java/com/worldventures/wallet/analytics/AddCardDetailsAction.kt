package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.wallet.domain.entity.record.Record

@AnalyticsEvent(action = "wallet:Add a Card:Card Detail", navigationState = true, trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class AddCardDetailsAction(record: Record, online: Boolean) : BaseCardDetailsAction() {

   @Attribute("addstate") internal val addState: String = if (online) "Online" else "Offline"

   init {
      fillRecordDetails(record)
   }
}
