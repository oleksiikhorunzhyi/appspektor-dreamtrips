package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Deprecated - use {@link AnalyticsInteractor}
 */
@Deprecated
public class TrackingHelper {

   private static final String KEY_ADOBE_TRACKER = "adobe_tracker";

   public static final String TYPE = "type";
   public static final String ID = "id";

   private static Map<String, Tracker> trackers = new HashMap<>();

   public static void init(Collection<Tracker> trackerSet) {
      Queryable.from(trackerSet).forEachR(tracker -> trackers.put(tracker.getKey(), tracker));
   }

   private TrackingHelper() {
   }

   public static void onCreate(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onCreate(activity);
      }
   }

   public static void onStart(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onStart(activity);
      }
   }

   public static void onStop(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onStop(activity);
      }
   }

   public static void onResume(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onResume(activity);
      }
   }

   public static void onPause(Activity activity) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onPause(activity);
      }
   }

   public static void onSaveInstanceState(Bundle outState) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onSaveInstanceState(outState);
      }
   }

   public static void onRestoreInstanceState(Bundle savedInstanceState) {
      for (Map.Entry<String, Tracker> entry : trackers.entrySet()) {
         entry.getValue().onRestoreInstanceState(savedInstanceState);
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Adobe tracking
   ///////////////////////////////////////////////////////////////////////////

   // ---------------- Messenger actions
   public static final String MESSENGER_ACTION_INBOX = "Messenger:Conversations"; //capture the number of conversations in the inbox
   public static final String MESSENGER_ACTION_ADD_FRIEND_TO_CHAT = "Messenger:Add Friends to Chat";
   public static final String MESSENGER_ACTION_CONVERSATION_FILTER = "Messenger:Conversation Filter";
   public static final String MESSENGER_ACTION_CONVERSATION_SORT = "Messenger:Conversation Type";
   public static final String MESSENGER_ACTION_GROUP_CHAT_SETINGS = "Messenger:Group Chat Settings";
   public static final String MESSENGER_ACTION_LEAVE = "Messenger:Leave Group Chat";
   public static final String MESSENGER_ACTION_TRANSLATION = "Messenger:View Conversation";

   // ---------------- Messenger attributes
   public static final String MESSENGER_ATTRIBUTE_NUMBER_OF_CONVERSATIONS = "numberconvo";
   public static final String MESSENGER_ATTRIBUTE_CONVERSATION_SORT_TYPE = "chatsort";
   public static final String MESSENGER_ATTRIBUTE_TRANSLATED = "translated";
   public static final String MESSENGER_ATTRIBUTE_TRANSLATION = "translation";

   public static final String MESSENGER_VALUE_ALL = "All Chats";
   public static final String MESSENGER_VALUE_GROUPS = "Group Chats";

   // Action/ViewState=Messenger:Inbox
   public static void setConversationCount(int count) {
      sendSimpleAttributetoAdobeTracker(MESSENGER_ACTION_INBOX, MESSENGER_ATTRIBUTE_NUMBER_OF_CONVERSATIONS, count);
   }

   public static void addPeopleToChat() {
      sendActionToAdobeTracker(MESSENGER_ACTION_ADD_FRIEND_TO_CHAT);
   }

   public static void conversationType(String value) {
      sendSimpleAttributetoAdobeTracker(MESSENGER_ACTION_CONVERSATION_SORT, MESSENGER_ATTRIBUTE_CONVERSATION_SORT_TYPE, value);
   }

   public static void leaveConversation() {
      sendActionToAdobeTracker(MESSENGER_ACTION_LEAVE);
   }

   public static void groupSettingsOpened() {
      sendActionToAdobeTracker(MESSENGER_ACTION_GROUP_CHAT_SETINGS);
   }

   public static void conversationSearchSelected() {
      sendActionToAdobeTracker(MESSENGER_ACTION_CONVERSATION_FILTER);
   }

   public static void translateMessage(String toLanguage) {
      Map<String, Object> data = new HashMap<>();
      data.put(MESSENGER_ATTRIBUTE_TRANSLATED, toLanguage);
      data.put(MESSENGER_ATTRIBUTE_TRANSLATION, "1");

      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, MESSENGER_ACTION_TRANSLATION, data);
   }

   // ---------------- Tracking helper methods

   public static void setUserId(String username, String userId) {
      HashMap<String, Object> headerData = new HashMap<>();
      headerData.put("member_id", username);
      headerData.put("old_member_id", userId);
      trackers.get(KEY_ADOBE_TRACKER).setHeaderData(headerData);
   }

   public static void sendActionToAdobe(String actionName, Map<String, Object> actionArgs) {
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, actionName, actionArgs);
   }

   private static void sendSimpleAttributetoAdobeTracker(String action, String attribute, Object value) {
      Map<String, Object> data = new HashMap<>();
      data.put(attribute, value);
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, action, data);
   }

   private static void sendActionToAdobeTracker(String action) {
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, action, null);
   }

   public static void clearHeaderData() {
      trackers.get(KEY_ADOBE_TRACKER).setHeaderData(null);
   }
}
