package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

@AnalyticsEvent(action = "wallet:Add a Card:Card Detail",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class AddCardDetailsAction extends BaseCardDetailsAction {

   @Attribute("addstate") final String addState;

   private AddCardDetailsAction(String addState) {
      this.addState = addState;
   }

   public static AddCardDetailsAction forBankCard(Record record, boolean online) {
      String addState = online ? "Online" : "Offline";
      AddCardDetailsAction action = new AddCardDetailsAction(addState);
      action.fillRecordDetails(record);
      return action;
   }
}
