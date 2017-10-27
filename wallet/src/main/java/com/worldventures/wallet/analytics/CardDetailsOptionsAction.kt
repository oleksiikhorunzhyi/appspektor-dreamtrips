package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.wallet.domain.entity.record.Record

@AnalyticsEvent(action = "wallet:Add a Card:Card Detail Options", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class CardDetailsOptionsAction(record: Record, isDefault: Boolean) : BaseCardDetailsWithDefaultAction() {

   @Attribute("paycardnickname") internal val nickname: String = record.nickName()
   @Attribute("setdefaultwhere") internal val setDefaultWhere = "In-App:Setup"
   @Attribute("changedefaultaddress") internal val changeDefaultAddress = "1"

   init {
      fillPaycardInfo(record, isDefault)
   }
}
