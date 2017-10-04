package com.messenger.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Messenger:Conversations", trackers = AdobeTracker.TRACKER_KEY)
public class ConversationsCountAction extends BaseAnalyticsAction {

   @Attribute("numberconvo") final String conversationsCount;

   public ConversationsCountAction(int conversationsCount) {
      this.conversationsCount = String.valueOf(conversationsCount);
   }
}
