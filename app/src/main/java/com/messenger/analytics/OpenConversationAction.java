package com.messenger.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AttributeMap;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "Messenger:View Conversation", trackers = AdobeTracker.TRACKER_KEY)
public class OpenConversationAction extends BaseAnalyticsAction {

   private static final String ATTRIBUTE_CONVERSATION_TYPE = "convotype";
   private static final String ATTRIBUTE_GROUP_CHAT_NAME = "groupchatname";

   private static final String MESSENGER_VALUE_INDIVIDUAL = "Individual";
   private static final String MESSENGER_VALUE_IN_DESTINATION_INDIVIDUAL = "InDestination-Individual";
   private static final String MESSENGER_VALUE_GROUP = "Group-%d";
   private static final String MESSENGER_VALUE_TRIP_CHAT = "DreamTrip-%d";
   private static final String MESSENGER_VALUE_TRIP_CHAT_WITH_HOST = "DreamTrip-InDestination-Group-%d";

   @AttributeMap final Map<String, String> attributeMap = new HashMap<>();

   protected OpenConversationAction(String conversationType, String conversationName) {
      this(conversationType);
      attributeMap.put(ATTRIBUTE_GROUP_CHAT_NAME, conversationName);
   }

   protected OpenConversationAction(String conversationType) {
      attributeMap.put(ATTRIBUTE_CONVERSATION_TYPE, conversationType);
   }

   public static OpenConversationAction forSingleConversation() {
      return new OpenConversationAction(MESSENGER_VALUE_INDIVIDUAL);
   }

   public static OpenConversationAction forSingleInDestinationConversation() {
      return new OpenConversationAction(MESSENGER_VALUE_IN_DESTINATION_INDIVIDUAL);
   }

   public static OpenConversationAction forGroupConversation(String subject, int count) {
      String type = String.format(MESSENGER_VALUE_GROUP, count);
      return new OpenConversationAction(type, subject);
   }

   public static OpenConversationAction forTripConversation(String subject, int count) {
      String type = String.format(MESSENGER_VALUE_TRIP_CHAT, count);
      return new OpenConversationAction(type, subject);
   }

   public static OpenConversationAction forTripWithHostConversation(String subject, int count) {
      String type = String.format(MESSENGER_VALUE_TRIP_CHAT_WITH_HOST, count);
      return new OpenConversationAction(type, subject);
   }
}
