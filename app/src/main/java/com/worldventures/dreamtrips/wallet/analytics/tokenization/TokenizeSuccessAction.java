package com.worldventures.dreamtrips.wallet.analytics.tokenization;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

@AnalyticsEvent(action = "wallet:tokenization success",
                trackers = AdobeTracker.TRACKER_KEY)
class TokenizeSuccessAction extends TokenizationCardAction {

   @Attribute("tokensuccess") private final int tokenSuccess = 1;
   @Attribute("successcondition") private final String successCondition;

   TokenizeSuccessAction(BankCard bankCard, ActionType actionType, boolean tokenize) {
      super(bankCard, actionType, tokenize);
      successCondition = generateCondition();
   }

}