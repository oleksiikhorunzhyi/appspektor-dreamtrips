package com.worldventures.wallet.analytics

import com.worldventures.core.service.analytics.Attribute
import com.worldventures.wallet.domain.entity.record.Record

abstract class BaseCardDetailsWithDefaultAction : BaseCardDetailsAction() {

   @Attribute("defaultpaycard") internal lateinit var defaultPaycard: String

   fun fillPaycardInfo(record: Record, isDefault: Boolean) {
      fillRecordDetails(record)
      defaultPaycard = if (isDefault) "Yes" else "No"
   }
}
