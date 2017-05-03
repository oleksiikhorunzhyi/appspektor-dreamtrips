package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

@AnalyticsEvent(action = "wallet:Add a Card:Card Detail",
                trackers = AdobeTracker.TRACKER_KEY)
public class AddCardDetailsAction extends BaseCardDetailsAction {

   @Attribute("addstate") final String addState;

   private AddCardDetailsAction(String addState) {
      this.addState = addState;
   }

   public static AddCardDetailsAction forBankCard(BankCard bankCard, boolean online) {
      String addState = online ? "Online" : "Offline";
      AddCardDetailsAction action = new AddCardDetailsAction(addState);
      action.fillPaycardInfo(bankCard);
      return action;
   }
}
