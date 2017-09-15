package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImagesType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Deprecated - use {@link AnalyticsInteractor}
 */
@Deprecated
public class TrackingHelper {

   private static final String KEY_ADOBE_TRACKER = "adobe_tracker";
   private static final String KEY_APPTENTIVE_TRACKER = "apptentive_tracker";

   public static final String CATEGORY_NAV_MENU = "nav_menu";

   public static final String ACTION_DREAMTRIPS_SOCIAL_DETAIL = "dreamtrips:socialdetail";
   public static final String ACTION_PHOTOS_YSBH = "photos-ysbh";
   public static final String ACTION_PHOTOS_ALL_USERS = "photos-allusers";
   public static final String ACTION_PHOTOS_MINE = "photos-mine";

   public static final String ACTION_BUCKET_LIST = "bucketlist";
   public static final String ACTION_BUCKET_PHOTO_UPLOAD_START = "bl_photo_upload_start";
   public static final String ACTION_BL_ITEM_VIEW = "bl_item_view";

   public static final String FIELD_MEMBER_ID = "member_id";
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
   // Tracker helpers
   ///////////////////////////////////////////////////////////////////////////

   private static void trackMemberAction(String category, String action, Map<String, Object> data) {
      trackers.get(KEY_APPTENTIVE_TRACKER).trackEvent(category, action, data);
   }

   private static void trackPageView(String category, String memberId, String action) {
      Map<String, Object> data = new HashMap<>();
      data.put(FIELD_MEMBER_ID, memberId);
      trackMemberAction(category, action, data);
   }

   private static void trackSpecificPageView(String category, String memberId, String action, String pageType, String id) {
      Map<String, Object> data = new HashMap<>();
      data.put(FIELD_MEMBER_ID, memberId);
      data.put(pageType, id);
      trackMemberAction(category, action, data);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Tracking actions deprecated
   ///////////////////////////////////////////////////////////////////////////

   public static void view(TripImagesType type, String id, String memberId) {
      if (type.equals(TripImagesType.YOU_SHOULD_BE_HERE)) {
         trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_YSBH, "view_ysbh_photo", id);
      } else if (type.equals(TripImagesType.MEMBERS_IMAGES)) {
         trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_ALL_USERS, "view_user_photo", id);
      } else if (type.equals(TripImagesType.ACCOUNT_IMAGES)) {
         trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_MINE, "view_user_photo", id);
      }
   }

   public static void flag(String id, String memberId) {
      Map<String, Object> data = new HashMap<>();
      data.put(FIELD_MEMBER_ID, memberId);
      data.put("flag_user_photo", id);
      trackMemberAction(CATEGORY_NAV_MENU, ACTION_PHOTOS_ALL_USERS, data);
   }

   public static void all(String memberId) {
      trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_ALL_USERS);
   }

   public static void bucketList(String memberId) {
      trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_BUCKET_LIST);
   }

   public static void bucketPhotoAction(String category, String type, String blType) {
      Map<String, Object> data = new HashMap<>();
      data.put(TYPE, type);
      data.put("bl_type", blType);
      trackMemberAction(category, null, data);
   }

   public static void bucketItemView(String type, String id) {
      Map<String, Object> data = new HashMap<>();
      data.put(ID, id);
      data.put("bl_type", type);
      trackMemberAction(ACTION_BL_ITEM_VIEW, null, data);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Adobe tracking
   ///////////////////////////////////////////////////////////////////////////

   // ---------------- DreamTrips actions
   public static final String ACTION_ACTIVITY_FEED = "activity_feed";
   public static final String ACTION_FRIENDS_ACTIVITY = "friends_activity";
   public static final String ACTION_TERMS = "Terms and Conditions";
   public static final String ACTION_SETTINGS = "Settings";
   public static final String ACTION_SETTINGS_GENERAL = "Settings:General";
   public static final String ACTION_SETTINGS_NOTIFICATIONS = "Settings:Notifications";

   public static final String ATTRIBUTE_LIST = "list";
   public static final String ATTRIBUTE_VIEW = "view";
   public static final String ATTRIBUTE_LIKE = "like";
   public static final String ATTRIBUTE_COMMENT = "comment";
   public static final String ATTRIBUTE_DELETE_COMMENT = "delete_comment";
   public static final String ATTRIBUTE_EDIT_COMMENT = "edit_comment";
   public static final String ATTRIBUTE_EDIT = "edit";
   public static final String ATTRIBUTE_DELETE = "delete";
   public static final String ATTRIBUTE_OPEN_FRIENDS = "open_friends";
   public static final String ATTRIBUTE_ADD_FRIENDS = "add_friends";
   public static final String ATTRIBUTE_SEARCH_FRIENDS = "search_friends";
   public static final String ATTRIBUTE_FILTER = "filter";
   public static final String ATTRIBUTE_ADD_FROM_POPULAR = "add_from_popular";
   public static final String ATTRIBUTE_UPLOAD_PHOTO = "upload_photo";
   public static final String ATTRIBUTE_SHARE = "share";
   public static final String ATTRIBUTE_MARK_AS_DONE = "mark_as_done";
   public static final String ATTRIBUTE_TERMS = "optinoptout";
   public static final String ATTRIBUTE_COMPLETE = "complete";
   public static final String ATTRIBUTE_VIEW_PHOTO = "view_photo";

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

   private static void sendSimpleAttributetoAdobeTracker(String action, String attribute) {
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, action, prepareAttributeMap(attribute));
   }

   private static void sendSimpleAttributetoAdobeTracker(String action, String attribute, Object value) {
      Map<String, Object> data = new HashMap<>();
      data.put(attribute, value);
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, action, data);
   }

   private static void sendActionToAdobeTracker(String action) {
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, action, null);
   }

   public static Map prepareAttributeMap(String attribute) {
      Map data = new HashMap<>();
      if (attribute != null) data.put(attribute, "1");
      return data;
   }

   // ---------------- Feed activity

   public static void sendActionItemFeed(String actionAttribute, String itemId, FeedEntityHolder.Type type) {
      Map<String, Object> data = new HashMap<>(2);
      switch (type) {
         case BUCKET_LIST_ITEM:
            data.put("bucket_list_id", itemId);
            break;
         case POST:
            data.put("post_id", itemId);
            break;
         case TRIP:
            data.put("trip_id", itemId);
            break;
         case PHOTO:
            data.put("photo_id", itemId);
            break;
      }
      data.put(actionAttribute, "1");
      String action;
      if (actionAttribute.equals(ATTRIBUTE_VIEW)) {
         action = ACTION_DREAMTRIPS_SOCIAL_DETAIL;
      } else {
         action = ACTION_ACTIVITY_FEED;
      }
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, action, data);
   }

   public static void tapFeedButton(String buttonAttribute) {
      sendSimpleAttributetoAdobeTracker(ACTION_FRIENDS_ACTIVITY, buttonAttribute);
   }

   public static void filterMyFriendsFeed(String filterType) {
      Map data = new HashMap<>();
      data.put("friends_filter_" + filterType, "1");
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_FRIENDS_ACTIVITY, data);
   }

   // ---------------- Book Travel

   public static void termsConditionsAction(boolean accepted) {
      Map data = new HashMap<>();
      data.put(ATTRIBUTE_TERMS, accepted ? "Opt In" : "Opt Out");
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_TERMS, data);
   }

   public static void settings() {
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_SETTINGS, null);
   }

   public static void settingsDetailed(SettingsGroup.Type type) {
      trackers.get(KEY_ADOBE_TRACKER)
            .trackEvent(null, type == SettingsGroup.Type.GENERAL ? ACTION_SETTINGS_GENERAL : ACTION_SETTINGS_NOTIFICATIONS, null);
   }

   // ---------------- Bucket List

   public static void viewBucketListScreen() {
      sendSimpleAttributetoAdobeTracker(ACTION_BUCKET_LIST, ATTRIBUTE_LIST);
   }

   public static void actionBucket(String eventType, String tab) {
      Map<String, Object> data = new HashMap<>();
      data.put(tab, "1");
      data.put(eventType, "1");
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_BUCKET_LIST, data);
   }

   public static void actionBucketItemPhoto(String eventType, String bucketItemId) {
      Map<String, Object> data = new HashMap<>();
      data.put("bucket_list_id", bucketItemId);
      data.put(eventType, "1");
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_BUCKET_LIST, data);
   }

   public static void actionBucketItem(String eventType, String bucketItemId) {
      Map<String, Object> data = new HashMap<>();
      data.put("bucket_list_id", bucketItemId);
      data.put(eventType, "1");
      trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_BUCKET_LIST, data);
   }

   public static void clearHeaderData() {
      trackers.get(KEY_ADOBE_TRACKER).setHeaderData(null);
   }
}
