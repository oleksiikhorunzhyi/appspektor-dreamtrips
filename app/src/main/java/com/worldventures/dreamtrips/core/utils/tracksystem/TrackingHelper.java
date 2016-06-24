package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.modules.trips.model.TripsFilterDataAnalyticsWrapper;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import org.intellij.lang.annotations.MagicConstant;

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

    public static final String ACTION_LOGIN = "login";

    public static final String ACTION_DREAMTRIPS = "dreamtrips";
    public static final String ACTION_DREAMTRIPS_SOCIAL_DETAIL = "dreamtrips:socialdetail";
    public static final String ACTION_DREAMTRIPS_TRIP_DETAIL = "dreamtrips:tripdetail";
    public static final String ACTION_PHOTOS_YSBH = "photos-ysbh";
    public static final String ACTION_PHOTOS_ALL_USERS = "photos-allusers";
    public static final String ACTION_PHOTOS_MINE = "photos-mine";
    public static final String ACTION_ENROLL_MERCHANT = "membership-enroll-merchant";
    public static final String ACTION_FAQ = "FAQ";
    public static final String ACTION_PRIVACY = "terms-privacy";
    public static final String ACTION_COOKIE = "terms-cookie";
    public static final String ACTION_SERVICE = "terms-service";

    public static final String ACTION_PHOTO_UPLOAD_START = "photo_upload_start";

    public static final String ACTION_PHOTOS_INSPR = "photos-inspireme";
    public static final String ACTION_INSPR_DETAILS = "inspireme_details";
    public static final String ACTION_INSPR_SHARE = "inspireme_share";

    public static final String ACTION_OTA = "ota_booking";
    public static final String ACTION_REP_ENROLL = "rep_enroll";
    public static final String ACTION_TRAINING_VIDEOS = "training_videos";

    public static final String ACTION_SS = "success_stories";
    public static final String ACTION_SS_VIEW = "success_story_view";
    public static final String ACTION_SS_LIKE = "success_story_like";
    public static final String ACTION_SS_UNLIKE = "success_story_like";

    public static final String ACTION_INVITE_CONTACTS = "invite_share_select_contacts";
    public static final String ACTION_TEMPLATE = "invite_share_template";
    public static final String ACTION_SEND_EMAIL = "invite_share_send_email";
    public static final String ACTION_SEND_SMS = "invite_share_send_sms";
    public static final String ACTION_RESEND_EMAIL = "invite_share_resend_email";
    public static final String ACTION_RESEND_SMS = "invite_share_resend_sms";

    public static final String ACTION_BUCKET_LIST = "bucketlist";
    public static final String ACTION_BUCKET_PHOTO_UPLOAD_START = "bl_photo_upload_start";
    public static final String ACTION_BL_ITEM_VIEW = "bl_item_view";
    public static final String ACTION_BL_POPULAR = "bucketlist_popular";
    public static final String ACTION_ADD_BL_START = "bl_add_item_start";
    public static final String ACTION_ADD_BL_FINISH = "bl_add_item_finish";

    public static final String ACTION_PROFILE = "profile";
    public static final String ACTION_PROFILE_PHOTO_UPLOAD_START = "profile_photo_upload_start";
    public static final String ACTION_PROFILE_PHOTO_UPLOAD_FINISH = "profile_photo_upload_finish";

    public static final String ACTION_MEMBERSHIP_VIDEOS = "membership:videos";
    public static final String ACTION_MEMBERSHIP_PODCASTS = "membership:podcasts";
    public static final String ACTION_MEMBERSHIP_PLAY = "member_videos_play";
    public static final String ACTION_MEMBERSHIP_LOAD_START = "member_videos_download_start";
    public static final String ACTION_MEMBERSHIP_LOAD_CANCELED = "member_videos_download_cancel";

    public static final String ACTION_360 = "videos_360";
    public static final String ACTION_360_PLAY = "videos_360_play";
    public static final String ACTION_360_LOAD_START = "videos_360_download_start";

    public static final String FIELD_MEMBER_ID = "member_id";
    public static final String TYPE = "type";
    public static final String ID = "id";

    private static Map<String, Tracker> trackers = new HashMap<>();

    public static void init(Collection<Tracker> trackerSet) {
        Queryable.from(trackerSet).forEachR(tracker ->
                trackers.put(tracker.getKey(), tracker));
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

    public static void login(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        trackMemberAction(ACTION_LOGIN, null, data);

        Map dataForAdobe = new HashMap<>();
        dataForAdobe.put(ATTRIBUTE_LOGIN, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_LOGIN, dataForAdobe);
    }

    public static void dreamTrips(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_DREAMTRIPS);
    }

    public static void trip(String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("view_trip", id);
        data.put(FIELD_MEMBER_ID, memberId);
        trackMemberAction(CATEGORY_NAV_MENU, ACTION_DREAMTRIPS, data);
    }

    public static void tripInfo(String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("trip_info", id);
        data.put(FIELD_MEMBER_ID, memberId);
        trackMemberAction(CATEGORY_NAV_MENU, ACTION_DREAMTRIPS, data);
    }

    public static void bookIt(String id, String memberId) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_DREAMTRIPS, "book_it", id);
    }

    public static void ysbh(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_YSBH);
    }

    public static void inspr(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_INSPR);
    }

    public static void insprDetails(String memberId, String id) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_INSPR, ACTION_INSPR_DETAILS, String.valueOf(id));
    }

    public static void insprShare(String id, @ShareType String type) {
        Map<String, Object> data = new HashMap<>();
        data.put(TYPE, resolveSharingType(type));
        data.put(ID, id);
        trackMemberAction(ACTION_INSPR_SHARE, null, data);
    }

    public static void view(TripImagesType type, String id, String memberId) {
        if (type.equals(TripImagesType.YOU_SHOULD_BE_HERE)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_YSBH, "view_ysbh_photo", id);
        } else if (type.equals(TripImagesType.MEMBERS_IMAGES)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_ALL_USERS, "view_user_photo", id);
        } else if (type.equals(TripImagesType.ACCOUNT_IMAGES)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_MINE, "view_user_photo", id);
        }
    }

    public static void like(TripImagesType type, String id, String memberId) {
        if (type.equals(TripImagesType.YOU_SHOULD_BE_HERE)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_YSBH, "like_ysbh_photo", id);
        } else if (type.equals(TripImagesType.MEMBERS_IMAGES)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_ALL_USERS, "like_user_photo", id);
        } else if (type.equals(TripImagesType.ACCOUNT_IMAGES)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_MINE, "like_user_photo", id);
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

    public static void mine(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_MINE);
    }

    public static void enrollMember(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_MEMBERSHIP_ENROLL);
    }

    public static void enrollMerchant(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_ENROLL_MERCHANT);
    }

    public static void profile(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_PROFILE);
    }

    public static void faq(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_FAQ);
    }

    public static void privacy(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_PRIVACY);
    }

    public static void cookie(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_COOKIE);
    }

    public static void service(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_SERVICE);
    }

    public static void photoUploadStarted(String type, String id) {
        Map<String, Object> data = new HashMap<>();
        data.put(TYPE, type);
        data.put(ID, id);
        trackMemberAction(ACTION_PHOTO_UPLOAD_START, null, data);
    }

    public static void video360(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_360);
    }

    public static void memberVideos(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_MEMBERSHIP_VIDEOS);
    }

    public static void videoAction(String action, String memberId, String label, String videoName) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, action, label, videoName);
    }

    public static void ota(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_OTA);
    }

    public static void enrollRep(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_REP_ENROLL);
    }

    public static void trainingVideos(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_TRAINING_VIDEOS);
    }

    public static void successStories(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_SS);
    }

    public static void viewSS(String memberId, int id) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_SS, ACTION_SS_VIEW, String.valueOf(id));
    }

    public static void unlikeSS(String memberId, int id) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_SS, ACTION_SS_UNLIKE, String.valueOf(id));
    }

    public static void likeSS(String memberId, int id) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_SS, ACTION_SS_LIKE, String.valueOf(id));
    }

    public static void inviteShare(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_REP_TOOLS_INVITE_SHARE);
    }

    public static void podcasts(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_MEMBERSHIP_PODCASTS);
    }

    public static void inviteShareContacts(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_INVITE_CONTACTS);
    }

    public static void inviteShareTemplate(String memberId, int templateId) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_REP_TOOLS_INVITE_SHARE, ACTION_TEMPLATE, String.valueOf(templateId));
    }

    public static void inviteShareAction(String category, int templateId, int count) {
        Map<String, Object> data = new HashMap<>();
        data.put("template_id", templateId);
        data.put("count", count);
        trackMemberAction(category, null, data);
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

    public static void bucketAddStart(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("bl_type", type);
        trackMemberAction(ACTION_ADD_BL_START, null, data);
    }

    public static void bucketAddFinish(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("bl_type", type);
        trackMemberAction(ACTION_ADD_BL_FINISH, null, data);
    }

    public static void profileUploadStart(String memberId) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PROFILE, ACTION_PROFILE_PHOTO_UPLOAD_START, null);
    }

    public static void profileUploadFinish(String memberId) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PROFILE, ACTION_PROFILE_PHOTO_UPLOAD_FINISH, null);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Adobe tracking
    ///////////////////////////////////////////////////////////////////////////

    // ---------------- DreamTrips actions
    public static final String ACTION_ACTIVITY_FEED = "activity_feed";
    public static final String ACTION_FRIENDS_ACTIVITY = "friends_activity";
    public static final String ACTION_NOTIFICATIONS = "notifications";
    public static final String ACTION_BOOK_TRAVEL = "book_travel";
    public static final String ACTION_TRIP_IMAGES = "trip_images";
    public static final String ACTION_MEMBER_IMAGES = "member_images";
    public static final String ACTION_MY_IMAGES = "my_images";
    public static final String ACTION_YSHB_IMAGES = "yshb_images";
    public static final String ACTION_INSPIRE_ME_IMAGES = "inspire_me_images";
    public static final String ACTION_360_VIDEOS = "360_videos";
    public static final String ACTION_MEMBERSHIP = "membership";
    public static final String ACTION_MEMBERSHIP_ENROLL = "membership:enroll-member";
    public static final String ACTION_LOGOUT = "Logout";
    public static final String ACTION_TERMS_PRIVACY = "terms-privacy";
    public static final String ACTION_TERMS_SERVICE = "terms_service";
    public static final String ACTION_TERMS_COOKIE = "terms-cookie";
    public static final String ACTION_REP_TOOLS_SUCCESS_STORY = "rep_tools:success_story";
    public static final String ACTION_REP_TOOLS_TRAINING_VIDEO = "rep_tools:training_video";
    public static final String ACTION_REP_TOOLS_REP_ENROLLMENT = "rep_tools:rep_enrollment";
    public static final String ACTION_REP_TOOLS_INVITE_SHARE = "rep_tools:invite_share";
    public static final String ACTION_FEEDBACK = "Send Feedback";
    public static final String ACTION_TERMS = "Terms and Conditions";
    public static final String ACTION_SETTINGS = "Settings";
    public static final String ACTION_SETTINGS_GENERAL = "Settings:General";
    public static final String ACTION_SETTINGS_NOTIFICATIONS = "Settings:Notifications";
    public static final String ACTION_PHOTO_UPLOAD = "photo_upload";

    // ---------------- DreamTrips attributes
    public static final String ATTRIBUTE_LOGIN = "login";
    public static final String ATTRIBUTE_LOGIN_ERROR = "login_error";
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
    public static final String ATTRIBUTE_UNFRIEND = "unfriend";
    public static final String ATTRIBUTE_REJECT_FRIEND_REQUEST = "reject_friend_request";
    public static final String ATTRIBUTE_CANCEL_FRIEND_REQUEST = "cancel_friend_request";
    public static final String ATTRIBUTE_SEARCH = "search";
    public static final String ATTRIBUTE_ADD = "add";
    public static final String ATTRIBUTE_FILTER = "filter";
    public static final String ATTRIBUTE_ADD_FROM_POPULAR = "add_from_popular";
    public static final String ATTRIBUTE_MAP = "map";
    public static final String ATTRIBUTE_BUCKET_LIST = "bucket_list";
    public static final String ATTRIBUTE_ADD_TO_BUCKET_LIST = "add_to_bucket_list";
    public static final String ATTRIBUTE_BOOK_IT = "book_it";
    public static final String ATTRIBUTE_LOAD_MORE = "load_more";
    public static final String ATTRIBUTE_UPLOAD_PHOTO = "upload_photo";
    public static final String ATTRIBUTE_SHARE = "share";
    public static final String ATTRIBUTE_DOWNLOAD = "download";
    public static final String ATTRIBUTE_MARK_AS_DONE = "mark_as_done";
    public static final String ATTRIBUTE_REASON = "feedbackreason";
    public static final String ATTRIBUTE_TERMS = "optinoptout";
    public static final String ATTRIBUTE_COMPLETE = "complete";
    public static final String ATTRIBUTE_FAVORITE = "favorite";
    public static final String ATTRIBUTE_DINING = "dining";
    public static final String ATTRIBUTE_ACTIVITIES = "activities";
    public static final String ATTRIBUTE_LOCATIONS = "locations";
    public static final String ATTRIBUTE_VIEW_PHOTO = "view_photo";
    public static final String ATTRIBUTE_SHOW_TRIPS = "show_trips";
    public static final String ATTRIBUTE_SHOW_BUCKETLIST = "show_bucketlist";
    public static final String ATTRIBUTE_NEW_POST = "new_post";
    public static final String ATTRIBUTE_SHOW_FRIENDS = "show_friends";
    public static final String ATTRIBUTE_SHARE_IMAGE = "share_image";
    public static final String ATTRIBUTE_FLAG_IMAGE = "flag_image";
    public static final String ATTRIBUTE_EDIT_IMAGE = "edit_image";
    public static final String ATTRIBUTE_DELETE_IMAGE = "delete_image";
    public static final String ATTRIBUTE_LIKE_IMAGE = "like_image";
    public static final String ATTRIBUTE_LOADING_ERROR = "loading_error";
    public static final String ATTRIBUTE_ADD_CONTACT = "add_contact";
    public static final String ATTRIBUTE_SHOW_ALL = "show_all";
    public static final String ATTRIBUTE_SHOW_FAVORITES = "show_favorites";
    public static final String ATTRIBUTE_FACEBOOK = "facebook";
    public static final String ATTRIBUTE_TWITTER = "twitter";
    public static final String ATTRIBUTE_SHARING_UNRESOLVED = "unknown";
    public static final String ATTRIBUTE_SELECT = "select";
    public static final String ATTRIBUTE_TRIP_FILTERS = "tripfilters";
    public static final String ATTRIBUTE_TRIP_REGION_FILTERS = "tripregionfilters";
    public static final String ATTRIBUTE_TRIP_THEME_FILTERS = "tripthemefilters";
    public static final String ATTRIBUTE_TRIP_SEARCH = "tripsearch";
    public static final String ATTRIBUTE_NUMBER_OF_UPLOADED_PHOTOS = "uploadamt";

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

    public static void conversationType(
            @MagicConstant(stringValues = {MESSENGER_VALUE_GROUPS, MESSENGER_VALUE_ALL}) String value) {
        sendSimpleAttributetoAdobeTracker(MESSENGER_ACTION_CONVERSATION_SORT,
                MESSENGER_ATTRIBUTE_CONVERSATION_SORT_TYPE, value);
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

    public static void setUserId(String userId) {
        HashMap<String, String> headerData = new HashMap<>(1);
        headerData.put("member_id", userId);
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

    public static String resolveSharingType(@ShareType String type) {
        switch (type) {
            case ShareType.FACEBOOK:
                return ATTRIBUTE_FACEBOOK;
            case ShareType.TWITTER:
                return ATTRIBUTE_TWITTER;
            default:
                return ATTRIBUTE_SHARING_UNRESOLVED;
        }
    }

    public static Map prepareAttributeMap(String attribute) {
        Map data = new HashMap<>();
        if (attribute != null) data.put(attribute, "1");
        return data;
    }

    // ---------------- Global category

    public static void loginError() {
        sendSimpleAttributetoAdobeTracker(ACTION_LOGIN, ATTRIBUTE_LOGIN_ERROR);
    }

    // ---------------- Feed activity

    public static void viewActivityFeedScreen() {
        sendSimpleAttributetoAdobeTracker(ACTION_ACTIVITY_FEED, ATTRIBUTE_LIST);
    }

    public static void sendActionItemFeed(@MagicConstant(stringValues = {ATTRIBUTE_VIEW, ATTRIBUTE_LIKE,
            ATTRIBUTE_COMMENT, ATTRIBUTE_EDIT_COMMENT, ATTRIBUTE_DELETE_COMMENT}) String actionAttribute,
                                          String itemId, FeedEntityHolder.Type type) {
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
        data.put(actionAttribute, itemId);
        String action;
        if (actionAttribute.equals(ATTRIBUTE_VIEW)) {
            action = ACTION_DREAMTRIPS_SOCIAL_DETAIL;
        } else {
            action = ACTION_ACTIVITY_FEED;
        }
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, action, data);
    }

    public static void tapFeedButton(@MagicConstant(stringValues = {ATTRIBUTE_OPEN_FRIENDS, ATTRIBUTE_ADD_FRIENDS,
            ATTRIBUTE_SEARCH_FRIENDS}) String buttonAttribute) {
        sendSimpleAttributetoAdobeTracker(ACTION_FRIENDS_ACTIVITY, buttonAttribute);
    }

    public static void tapMyFriendsButtonFeed(@MagicConstant(stringValues = {ATTRIBUTE_UNFRIEND, ATTRIBUTE_REJECT_FRIEND_REQUEST,
            ATTRIBUTE_CANCEL_FRIEND_REQUEST}) String action) {
        sendSimpleAttributetoAdobeTracker(ACTION_FRIENDS_ACTIVITY, action);
    }

    public static void filterMyFriendsFeed(String filterType) {
        Map data = new HashMap<>();
        data.put("friends_filter_" + filterType, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_FRIENDS_ACTIVITY, data);
    }

    // ---------------- Dream Trips

    public static void viewDreamTripsScreen() {
        sendSimpleAttributetoAdobeTracker(ACTION_DREAMTRIPS, ATTRIBUTE_LIST);
    }

    public static void viewTripDetails(String tripId, String tripName, String searchQuery,
                                       TripsFilterDataAnalyticsWrapper filterData) {
        Map data = new HashMap<>();
        data.put("trip_id", tripName + "-" + tripId);
        data.put(ATTRIBUTE_VIEW, 1);
        if (!TextUtils.isEmpty(searchQuery)) {
            data.put(ATTRIBUTE_TRIP_SEARCH, searchQuery);
        }
        data.put(ATTRIBUTE_TRIP_FILTERS, filterData.getFilterAnalyticString());
        data.put(ATTRIBUTE_TRIP_REGION_FILTERS, filterData.getAcceptedRegionsAnalyticString());
        data.put(ATTRIBUTE_TRIP_THEME_FILTERS, filterData.getAcceptedActivitiesAnalyticString());
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_DREAMTRIPS_TRIP_DETAIL, data);
    }

    public static void tapDreamTripsButton(@MagicConstant(stringValues = {ATTRIBUTE_SEARCH, ATTRIBUTE_FILTER,
            ATTRIBUTE_MAP}) String buttonType) {
        sendSimpleAttributetoAdobeTracker(ACTION_DREAMTRIPS, buttonType);
    }

    public static void actionItemDreamtrips(@MagicConstant(stringValues = {ATTRIBUTE_BUCKET_LIST,
            ATTRIBUTE_FAVORITE}) String eventType, String tripId, String tripName) {
        Map data = new HashMap<>();
        data.put("trip_id", tripName + "-" + tripId);
        data.put(eventType, tripId);
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_DREAMTRIPS, data);
    }

    public static void actionBookIt(@MagicConstant(stringValues = {ATTRIBUTE_BOOK_IT})
                                    String eventType, String tripId, String tripName) {
        Map data = new HashMap<>();
        data.put("trip_id", tripName + "-" + tripId);
        data.put(eventType, 1);
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_DREAMTRIPS, data);
    }

    public static void actionFilterTrips(TripsFilterDataAnalyticsWrapper filterData) {
        Map data = new HashMap<>();
        data.put(ATTRIBUTE_FILTER, 1);
        data.put(ATTRIBUTE_TRIP_FILTERS, filterData.getFilterAnalyticString());
        data.put(ATTRIBUTE_TRIP_REGION_FILTERS, filterData.getAcceptedRegionsAnalyticString());
        data.put(ATTRIBUTE_TRIP_THEME_FILTERS, filterData.getAcceptedActivitiesAnalyticString());
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_DREAMTRIPS, data);
    }

    // ---------------- Notifications

    public static void viewNotificationsScreen() {
        sendSimpleAttributetoAdobeTracker(ACTION_NOTIFICATIONS, ATTRIBUTE_LIST);
    }

    public static void loadMoreNotifications() {
        sendSimpleAttributetoAdobeTracker(ACTION_NOTIFICATIONS, ATTRIBUTE_LOAD_MORE);
    }

    // ---------------- Book Travel

    public static void actionBookTravelScreen(@MagicConstant(stringValues = {ATTRIBUTE_VIEW}) String eventType) {
        sendSimpleAttributetoAdobeTracker(ACTION_BOOK_TRAVEL, eventType);
    }

    // ---------------- Trip Images

    public static void viewTripImagesScreen() {
        sendSimpleAttributetoAdobeTracker(ACTION_TRIP_IMAGES, ATTRIBUTE_LIST);
    }

    public static void selectTripImagesTab(@MagicConstant(stringValues = {ACTION_MEMBER_IMAGES,
            ACTION_MY_IMAGES, ACTION_YSHB_IMAGES, ACTION_INSPIRE_ME_IMAGES, ACTION_360_VIDEOS}) String tab) {
        sendSimpleAttributetoAdobeTracker(tab, ATTRIBUTE_LIST);
    }

    public static void actionTripImage(@MagicConstant(stringValues = {ATTRIBUTE_VIEW, ATTRIBUTE_SHARE_IMAGE, ATTRIBUTE_FLAG_IMAGE,
            ATTRIBUTE_EDIT_IMAGE, ATTRIBUTE_DELETE_IMAGE, ATTRIBUTE_LIKE_IMAGE}) String eventType, String tripImageId) {
        Map data = new HashMap<>();
        data.put("photo_id", tripImageId);
        data.put(eventType, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_MEMBER_IMAGES, data);
    }

    public static void viewTripImage(@MagicConstant(stringValues = {ACTION_YSHB_IMAGES, ACTION_INSPIRE_ME_IMAGES})
                                     String tab, String imageId) {
        Map data = new HashMap<>();
        data.put("image_id", imageId);
        data.put(ATTRIBUTE_VIEW, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, tab, data);
    }

    public static void uploadTripImagePhoto(@MagicConstant(stringValues = {ACTION_MEMBER_IMAGES, ACTION_MY_IMAGES})
                                            String actionTab) {
        Map data = new HashMap<>();
        data.put(actionTab, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_MEMBER_IMAGES, data);
    }

    public static void actionPhotosUploaded(int uploadedPhotosCount) {
        Map data = new HashMap<>();
        data.put(ATTRIBUTE_NUMBER_OF_UPLOADED_PHOTOS, uploadedPhotosCount);
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_PHOTO_UPLOAD, data);
    }

    public static void sendFeedback(int reason) {
        Map data = new HashMap<>();
        data.put(ATTRIBUTE_REASON, String.valueOf(reason));
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_FEEDBACK, data);
    }

    public static void termsConditionsAction(boolean accepted) {
        Map data = new HashMap<>();
        data.put(ATTRIBUTE_TERMS, accepted ? "Opt In" : "Opt Out");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_TERMS, data);
    }

    public static void settings() {
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_SETTINGS, null);
    }

    public static void settingsDetailed(SettingsGroup.Type type) {
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null,
                type == SettingsGroup.Type.GENERAL
                        ? ACTION_SETTINGS_GENERAL
                        : ACTION_SETTINGS_NOTIFICATIONS, null);
    }

    public static void actionTripVideo(@MagicConstant(stringValues = {ATTRIBUTE_VIEW, ATTRIBUTE_DOWNLOAD})
                                       String eventType, String videoName) {
        Map<String, Object> data = new HashMap<>();
        data.put("video_id", videoName);
        data.put(eventType, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_360_VIDEOS, data);
    }

    // ---------------- Membership

    public static void viewMembershipScreen(@MagicConstant(stringValues = {ACTION_MEMBERSHIP, ACTION_MEMBERSHIP_ENROLL})
                                            String tab) {
        sendSimpleAttributetoAdobeTracker(tab, ATTRIBUTE_LIST);
    }

    public static void actionMembershipEnrollMemberScreen(@MagicConstant(stringValues = {ATTRIBUTE_VIEW}) String eventType) {
        sendSimpleAttributetoAdobeTracker(ACTION_MEMBERSHIP_ENROLL, eventType);
    }

    public static void actionMembershipEnrollMerchantScreen(@MagicConstant(stringValues = {ATTRIBUTE_VIEW}) String eventType) {
        sendSimpleAttributetoAdobeTracker("local.merchant.view", eventType);
    }

    public static void actionMembershipVideo(@MagicConstant(stringValues = {ATTRIBUTE_VIEW, ATTRIBUTE_DOWNLOAD})
                                             String eventType, String videoName) {
        Map<String, Object> data = new HashMap<>();
        data.put("video_id", videoName);
        data.put(eventType, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_MEMBERSHIP, data);
    }

    // ---------------- Bucket List

    public static void viewBucketListScreen() {
        sendSimpleAttributetoAdobeTracker(ACTION_BUCKET_LIST, ATTRIBUTE_LIST);
    }

    public static void actionBucket(@MagicConstant(stringValues = {ATTRIBUTE_ADD, ATTRIBUTE_ADD_FROM_POPULAR,
            ATTRIBUTE_FILTER}) String eventType, @MagicConstant(stringValues = {ATTRIBUTE_LOCATIONS,
            ATTRIBUTE_ACTIVITIES, ATTRIBUTE_DINING}) String tab) {
        Map<String, Object> data = new HashMap<>();
        data.put(tab, "1");
        data.put(eventType, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_BUCKET_LIST, data);
    }

    public static void actionBucketItemPhoto(@MagicConstant(stringValues = {ATTRIBUTE_VIEW_PHOTO, ATTRIBUTE_UPLOAD_PHOTO}) String eventType,
                                             String bucketItemId) {
        Map<String, Object> data = new HashMap<>();
        data.put("bucket_list_id", bucketItemId);
        data.put(eventType, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_BUCKET_LIST, data);
    }

    public static void actionBucketItem(@MagicConstant(stringValues = {ATTRIBUTE_VIEW, ATTRIBUTE_SHARE, ATTRIBUTE_MARK_AS_DONE,
            ATTRIBUTE_EDIT, ATTRIBUTE_DELETE, ATTRIBUTE_COMPLETE}) String eventType, String bucketItemId) {
        Map<String, Object> data = new HashMap<>();
        data.put("bucket_list_id", bucketItemId);
        data.put(eventType, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_BUCKET_LIST, data);
    }

    // ---------------- My Profile

    public static void viewMyProfileScreen() {
        sendSimpleAttributetoAdobeTracker(ACTION_PROFILE, ATTRIBUTE_VIEW);
    }

    public static void tapMyProfileButton(@MagicConstant(stringValues = {ATTRIBUTE_SHOW_TRIPS, ATTRIBUTE_SHOW_BUCKETLIST,
            ATTRIBUTE_NEW_POST, ATTRIBUTE_SHOW_FRIENDS}) String buttonType) {
        sendSimpleAttributetoAdobeTracker(ACTION_PROFILE, buttonType);
    }

    public static void logout() {
        sendSimpleAttributetoAdobeTracker(ACTION_LOGOUT, null);
        trackers.get(KEY_ADOBE_TRACKER).setHeaderData(null);
    }

    // ---------------- Rep Tools

    public static void applyFilterRepTools(@MagicConstant(stringValues = {ATTRIBUTE_SHOW_ALL, ATTRIBUTE_SHOW_FAVORITES})
                                           String filterCategory) {
        sendSimpleAttributetoAdobeTracker(ACTION_REP_TOOLS_SUCCESS_STORY, filterCategory);
    }

    public static void searchRepTools(@MagicConstant(stringValues = {ACTION_REP_TOOLS_SUCCESS_STORY, ACTION_REP_TOOLS_INVITE_SHARE})
                                      String tab) {
        sendSimpleAttributetoAdobeTracker(tab, ATTRIBUTE_SEARCH);
    }

    public static void viewSuccessStory(String storyId) {
        Map data = new HashMap<>();
        data.put("story_id", storyId);
        data.put(ATTRIBUTE_VIEW, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_REP_TOOLS_SUCCESS_STORY, data);
    }

    public static void shareSuccessStory(@ShareType String socialNet, String storyId) {
        Map data = new HashMap<>();
        data.put(resolveSharingType(socialNet), storyId);
        data.put(ATTRIBUTE_SHARE, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_REP_TOOLS_SUCCESS_STORY, data);
    }

    public static void favoriteSuccessStory(String storyId) {
        Map data = new HashMap<>();
        data.put(ATTRIBUTE_FAVORITE, storyId);
        data.put(ATTRIBUTE_FAVORITE, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_REP_TOOLS_SUCCESS_STORY, data);
    }

    public static void viewRepToolsTrainingVideoScreen() {
        sendSimpleAttributetoAdobeTracker(ACTION_TRAINING_VIDEOS, ATTRIBUTE_LIST);
    }

    public static void actionRepToolsTrainingVideo(@MagicConstant(stringValues = {ATTRIBUTE_VIEW, ATTRIBUTE_DOWNLOAD})
                                                   String action, String videoName) {
        Map data = new HashMap<>();
        data.put("video_id", videoName);
        data.put(action, "1");
        trackers.get(KEY_ADOBE_TRACKER).trackEvent(null, ACTION_REP_TOOLS_TRAINING_VIDEO, data);
    }

    public static void actionRepToolsEnrollment(@MagicConstant(stringValues = {ATTRIBUTE_VIEW}) String eventType) {
        sendSimpleAttributetoAdobeTracker(ACTION_REP_TOOLS_REP_ENROLLMENT, eventType);
    }

    public static void actionRepToolsInviteShare(@MagicConstant(stringValues = {ATTRIBUTE_VIEW, ATTRIBUTE_ADD_CONTACT})
                                                 String eventType) {
        sendSimpleAttributetoAdobeTracker(ACTION_REP_TOOLS_INVITE_SHARE, eventType);
    }

    // ---------------- FAQ

    public static void actionFaq() {
        sendSimpleAttributetoAdobeTracker(ACTION_FAQ, null);
    }

    // ---------------- Terms

    public static void actionTermsTab(@MagicConstant(stringValues = {ACTION_TERMS_PRIVACY, ACTION_TERMS_SERVICE,
            ACTION_TERMS_COOKIE}) String tab, @MagicConstant(stringValues = {ATTRIBUTE_VIEW}) String eventType) {
        sendSimpleAttributetoAdobeTracker(tab, eventType);
    }
}
