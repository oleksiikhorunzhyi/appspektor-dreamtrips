package com.messenger.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Messenger:Conversation Type", trackers = AdobeTracker.TRACKER_KEY)
public class ConversationTypeFilterSelectedAction extends BaseAnalyticsAction {

   @Attribute("chatsort") final String sortType;

   protected ConversationTypeFilterSelectedAction(String sortType) {
      this.sortType = sortType;
   }

   public static ConversationTypeFilterSelectedAction groupChats() {
      return new ConversationTypeFilterSelectedAction("Group Chats");
   }

   public static ConversationTypeFilterSelectedAction allChats() {
      return new ConversationTypeFilterSelectedAction("All Chats");
   }
}
