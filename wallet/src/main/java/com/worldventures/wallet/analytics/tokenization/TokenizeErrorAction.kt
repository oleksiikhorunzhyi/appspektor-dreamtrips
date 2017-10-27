package com.worldventures.wallet.analytics.tokenization

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.domain.entity.record.Record

@AnalyticsEvent(action = "wallet:error", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class TokenizeErrorAction(record: Record, actionType: ActionType, tokenize: Boolean)
   : TokenizationCardAction(record, actionType, tokenize) {

   @Attribute("dtaerror") private val dtaError = 1
   @Attribute("errorcondition") private val errorCondition: String = generateCondition()

}