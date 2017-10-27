package com.worldventures.wallet.analytics.tokenization

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.domain.entity.record.Record

@AnalyticsEvent(action = "wallet:tokenization success", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
internal class TokenizeSuccessAction(record: Record, actionType: ActionType, tokenize: Boolean)
   : TokenizationCardAction(record, actionType, tokenize) {

   @Attribute("tokensuccess") private val tokenSuccess = 1
   @Attribute("successcondition") private val successCondition: String = generateCondition()

}