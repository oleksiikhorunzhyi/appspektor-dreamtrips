package com.messenger.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Messenger:Conversation Type", trackers = AdobeTracker.TRACKER_KEY)
public class ConversationTypeFilterSelectedAction extends BaseAnalyticsAction {

   @Attribute("chatsort")
   final String sortType;

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
