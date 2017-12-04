package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.Attribute
import com.worldventures.wallet.domain.entity.record.Record

abstract class BaseSetDefaultCardAction : BaseCardDetailsWithDefaultAction() {

   @Attribute("setdefaultcard")
   val setDefaultCard = "1"
   @Attribute("setdefaultwhere")
   var setDefaultWhere: String? = null

   override fun fillRecordDetails(record: Record) {
      super.fillRecordDetails(record)
      defaultPaycard = "Yes"
   }
}
