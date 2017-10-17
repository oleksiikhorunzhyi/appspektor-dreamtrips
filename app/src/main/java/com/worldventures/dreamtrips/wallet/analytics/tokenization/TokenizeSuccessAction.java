package com.worldventures.dreamtrips.wallet.analytics.tokenization;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

@AnalyticsEvent(action = "wallet:tokenization success",
                trackers = AdobeTracker.TRACKER_KEY)
class TokenizeSuccessAction extends TokenizationCardAction {

   @Attribute("tokensuccess") final int tokenSuccess = 1;
   @Attribute("successcondition") final String successCondition;

   TokenizeSuccessAction(Record record, ActionType actionType, boolean tokenize) {
      super(record, actionType, tokenize);
      successCondition = generateCondition();
   }

}
