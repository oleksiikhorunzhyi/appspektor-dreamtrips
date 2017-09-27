package com.messenger.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Messenger:Conversations", trackers = AdobeTracker.TRACKER_KEY)
public class ConversationsCountAction extends BaseAnalyticsAction {

   @Attribute("numberconvo")
   final String conversationsCount;

   public ConversationsCountAction(int conversationsCount) {
      this.conversationsCount = String.valueOf(conversationsCount);
   }
}
