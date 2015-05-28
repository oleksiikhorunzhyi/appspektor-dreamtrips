package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;

import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackingHelper {

    public static final String ACTION_LOGIN = "login";

    public static final String CATEGORY_NAV_MENU = "nav_menu";

    public static final String ACTION_DREAMTRIPS = "dreamtrips";
    public static final String ACTION_PHOTOS_YSBH = "photos-ysbh";
    public static final String ACTION_PHOTOS_ALL_USERS = "photos-allusers";
    public static final String ACTION_PHOTOS_MINE = "photos-mine";
    public static final String ACTION_ENROLL = "membership-enroll";
    public static final String ACTION_FAQ = "faq";
    public static final String ACTION_PRIVACY = "terms-privacy";
    public static final String ACTION_COOKIE = "terms-cookie";
    public static final String ACTION_SERVICE = "terms-service";

    public static final String ACTION_PHOTO_UPLOAD = "photo_upload_start";
    public static final String ACTION_PHOTO_FINISHED = "photo_upload_finish";

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
    public static final String ACTION_SS_SHARE = "success_story_share";

    public static final String ACTION_INVITE = "invite_share";
    public static final String ACTION_INVITE_CONTACTS = "invite_share_select_contacts";
    public static final String ACTION_TEMPLATE = "invite_share_template";
    public static final String ACTION_SEND_EMAIL = "invite_share_send_email";
    public static final String ACTION_SEND_SMS = "invite_share_send_sms";
    public static final String ACTION_RESEND_EMAIL = "invite_share_resend_email";
    public static final String ACTION_RESEND_SMS = "invite_share_resend_sms";

    public static final String ACTION_BUCKET_LIST = "bucketlist";
    public static final String ACTION_BUCKET_PHOTO_UPLOAD_START = "bl_photo_upload_start";
    public static final String ACTION_BUCKET_PHOTO_UPLOAD_FINISH = "bl_photo_upload_start";
    public static final String ACTION_BUCKET_PHOTO_UPLOAD_CANCEL = "bl_photo_upload_start";
    public static final String ACTION_BL_ITEM_VIEW = "bl_item_view";
    public static final String ACTION_BL_POPULAR = "bucketlist_popular";
    public static final String ACTION_ADD_BL_START = "bl_add_item_start";
    public static final String ACTION_ADD_BL_FINISH = "bl_add_item_finish";

    public static final String ACTION_PROFILE = "profile";
    public static final String ACTION_PROFILE_PHOTO_UPLOAD_START = "profile_photo_upload_start";
    public static final String ACTION_PROFILE_PHOTO_UPLOAD_FINISH = "profile_photo_upload_finish";

    public static final String ACTION_MEMBERSHIP = "membership-videos";
    public static final String ACTION_MEMBERSHIP_PLAY = "member_videos_play";
    public static final String ACTION_MEMBERSHIP_LOAD_START = "member_videos_download_start";
    public static final String ACTION_MEMBERSHIP_LOAD_FINISHED = "member_videos_download_finish";
    public static final String ACTION_MEMBERSHIP_LOAD_CANCELED = "member_videos_download_cancel";

    public static final String ACTION_360 = "videos_360";
    public static final String ACTION_360_PLAY = "videos_360_play";
    public static final String ACTION_360_LOAD_START = "videos_360_download_start";
    public static final String ACTION_360_LOAD_FINISHED = "videos_360_download_finish";
    public static final String ACTION_360_LOAD_CANCELED = "videos_360_download_cancel";

    public static final String FIELD_MEMBER_ID = "member_id";
    public static final String TYPE = "type";
    public static final String ID = "id";

    private static List<ITracker> trackers = new ArrayList<>();

    static {
        trackers.add(new AdobeTracker());
        trackers.add(new ApptentiveTracker());
        trackers.add(new GoogleTracker());
    }

    private TrackingHelper() {
    }

    public static void onCreate(BaseActivity activity) {
        for (ITracker tracker : trackers) {
            tracker.onCreate(activity);
        }
    }

    public static void onStart(Activity activity) {
        for (ITracker tracker : trackers) {
            tracker.onStart(activity);
        }
    }

    public static void onStop(Activity activity) {
        for (ITracker tracker : trackers) {
            tracker.onStop(activity);
        }
    }

    public static void onResume(Activity activity) {
        for (ITracker tracker : trackers) {
            tracker.onResume(activity);
        }
    }

    public static void onPause(Activity activity) {
        for (ITracker tracker : trackers) {
            tracker.onPause(activity);
        }
    }

    ///////////
    /// TRACKER HELPERS
    //////////

    private static void trackMemberAction(String category, String action, Map<String, Object> data) {
        for (ITracker tracker : trackers) {
            tracker.trackEvent(category, action, data);
        }
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


    ///////////
    /// TRACKING ACTIONS
    //////////

    public static void login(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        trackMemberAction(ACTION_LOGIN, null, data);
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

    public static void insprShare(String id, String type) {
        Map<String, Object> data = new HashMap<>();
        data.put(TYPE, type);
        data.put(ID, id);
        trackMemberAction(ACTION_INSPR_SHARE, null, data);
    }

    public static void view(TripImagesListFragment.Type type, String id, String memberId) {
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_YSBH, "view_ysbh_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_ALL_USERS, "view_user_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_MINE, "view_user_photo", id);
        }
    }

    public static void like(TripImagesListFragment.Type type, String id, String memberId) {
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_YSBH, "like_ysbh_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_ALL_USERS, "like_user_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_MINE, "like_user_photo", id);
        }
    }

    public static void flag(TripImagesListFragment.Type type, String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            data.put(FIELD_MEMBER_ID, memberId);
            data.put("flag_user_photo", id);
            trackMemberAction(CATEGORY_NAV_MENU, ACTION_PHOTOS_ALL_USERS, data);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            data.put(FIELD_MEMBER_ID, memberId);
            data.put("flag_user_photo", id);
            trackMemberAction(CATEGORY_NAV_MENU, ACTION_PHOTOS_MINE, data);
        }
    }

    public static void all(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_ALL_USERS);
    }

    public static void mine(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_PHOTOS_MINE);
    }

    public static void enroll(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_ENROLL);
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
        trackMemberAction(ACTION_PHOTO_UPLOAD, null, data);
    }

    public static void photoUploadFinished(String type, String id) {
        Map<String, Object> data = new HashMap<>();
        data.put(TYPE, type);
        data.put(ID, id);
        trackMemberAction(ACTION_PHOTO_FINISHED, null, data);
    }

    public static void video360(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_360);
    }

    public static void memberVideos(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_MEMBERSHIP);
    }

    public static void videoAction(String action, String memberId, String label, String videoName) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, action, label, videoName);
    }

    public static void ota(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_OTA);
    }

    public static void repEnroll(String memberId) {
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
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_INVITE);
    }

    public static void inviteShareContacts(String memberId) {
        trackPageView(CATEGORY_NAV_MENU, memberId, ACTION_INVITE_CONTACTS);
    }

    public static void inviteShareTemplate(String memberId, int templateId) {
        trackSpecificPageView(CATEGORY_NAV_MENU, memberId, ACTION_INVITE, ACTION_TEMPLATE, String.valueOf(templateId));
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

    public static void bucketItemView(String type, int id) {
        Map<String, Object> data = new HashMap<>();
        data.put(ID, id);
        data.put("bl_type", type);
        trackMemberAction(ACTION_BL_ITEM_VIEW, null, data);
    }

    public static void bucketPopular(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("bl_type", type);
        trackMemberAction(ACTION_BL_POPULAR, null, data);
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

}
