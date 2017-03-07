package com.worldventures.dreamtrips.wallet.analytics.tokenization;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

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