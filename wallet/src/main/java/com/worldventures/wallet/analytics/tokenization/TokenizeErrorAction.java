package com.worldventures.wallet.analytics.tokenization;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.domain.entity.record.Record;

@AnalyticsEvent(action = "wallet:error",
                trackers = AdobeTracker.TRACKER_KEY)
class TokenizeErrorAction extends TokenizationCardAction {

   @Attribute("dtaerror") private final int dtaError = 1;
   @Attribute("errorcondition") private final String errorCondition;

   TokenizeErrorAction(Record record, ActionType actionType, boolean tokenize) {
      super(record, actionType, tokenize);
      errorCondition = generateCondition();
   }

}