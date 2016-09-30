package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

@AnalyticsEvent(action = "wallet:Add Default Card:Set as Default Card",
                trackers = AdobeTracker.TRACKER_KEY)
public class SetDefaultCardAction extends BaseSetDefaultCardAction {

   private SetDefaultCardAction() {
   }

   public static SetDefaultCardAction forBankCard(BankCard bankCard) {
      SetDefaultCardAction action = new SetDefaultCardAction();
      action.setDefaultWhere = "In-App:Wallet Home";
      action.fillPaycardInfo(bankCard);
      return action;
   }
}
