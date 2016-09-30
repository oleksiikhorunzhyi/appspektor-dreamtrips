package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

@AnalyticsEvent(action = "wallet:Card Detail:Set as Default Card",
                trackers = AdobeTracker.TRACKER_KEY)
public class ChangeDefaultCardAction extends BaseSetDefaultCardAction {

   private ChangeDefaultCardAction() {
   }

   public static ChangeDefaultCardAction forBankCard(BankCard bankCard) {
      ChangeDefaultCardAction action = new ChangeDefaultCardAction();
      action.setDefaultWhere = "In-App:Card Detail";
      action.fillPaycardInfo(bankCard);
      return action;
   }
}
