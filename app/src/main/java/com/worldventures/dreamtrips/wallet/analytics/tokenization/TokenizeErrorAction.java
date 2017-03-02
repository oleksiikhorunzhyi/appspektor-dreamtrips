package com.worldventures.dreamtrips.wallet.analytics.tokenization;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

@AnalyticsEvent(action = "wallet:error",
                trackers = AdobeTracker.TRACKER_KEY)
class TokenizeErrorAction extends TokenizationCardAction {

   @Attribute("dtaerror") private final int dtaError = 1;
   @Attribute("errorcondition") private final String errorCondition;

   TokenizeErrorAction(BankCard bankCard, ActionType actionType, boolean tokenize) {
      super(bankCard, actionType, tokenize);
      errorCondition = generateCondition();
   }

}