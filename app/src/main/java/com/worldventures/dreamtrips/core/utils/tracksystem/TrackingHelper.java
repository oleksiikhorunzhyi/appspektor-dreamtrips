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
    public static final String ACTION_DREAMTRIPS = "nav_menu:dreamtrips";
    public static final String ACTION_PHOTOS_YSBH = "nav_menu:photos-ysbh";
    public static final String ACTION_PHOTOS_ALL_USERS = "nav_menu:photos-allusers";
    public static final String ACTION_PHOTOS_MINE = "nav_menu:photos-mine";
    public static final String ACTION_ENROLL = "nav_menu:membership-enroll";
    public static final String ACTION_FAQ = "nav_menu:faq";
    public static final String ACTION_PRIVACY = "nav_menu:terms-privacy";
    public static final String ACTION_COOKIE = "nav_menu:terms-cookie";
    public static final String ACTION_SERVICE = "nav_menu:terms-service";

    public static final String ACTION_PHOTO_UPLOAD = "photo_upload_start";
    public static final String ACTION_PHOTO_FINISHED = "photo_upload_finish";

    public static final String ACTION_PHOTOS_INSPR = "nav_menu:photos-inspireme";
    public static final String ACTION_INSPR_DETAILS = "inspireme_details:%s";
    public static final String ACTION_INSPR_SHARE = "inspireme_share";

    public static final String ACTION_OTA = "nav_menu:ota_booking";
    public static final String ACTION_REP_ENROLL = "nav_menu:rep_enroll";
    public static final String ACTION_TRAINING_VIDEOS = "nav_menu:training_videos";

    public static final String ACTION_SS = "nav_menu:success_stories";
    public static final String ACTION_SS_VIEW = "success_story_view:%d";
    public static final String ACTION_SS_LIKE = "success_story_like:%d";
    public static final String ACTION_SS_UNLIKE = "success_story_like:%d";
    public static final String ACTION_SS_SHARE = "success_story_share";

    public static final String ACTION_INVITE = "nav_menu:invite_share";
    public static final String ACTION_INVITE_CONTACTS = "invite_share_select_contacts";
    public static final String ACTION_TEMPLATE = "invite_share_template:%d";
    public static final String ACTION_SEND_EMAIL = "invite_share_send_email";
    public static final String ACTION_SEND_SMS = "invite_share_send_sms";
    public static final String ACTION_RESEND_EMAIL = "invite_share_resend_email";
    public static final String ACTION_RESEND_SMS = "invite_share_resend_sms";

    public static final String ACTION_BUCKET_LIST = "nav_menu:bucketlist";
    public static final String ACTION_BUCKET_PHOTO_UPLOAD_START = "bl_photo_upload_start";
    public static final String ACTION_BUCKET_PHOTO_UPLOAD_FINISH = "bl_photo_upload_start";
    public static final String ACTION_BUCKET_PHOTO_UPLOAD_CANCEL = "bl_photo_upload_start";
    public static final String ACTION_BL_ITEM_VIEW = "bl_item_view";
    public static final String ACTION_BL_POPULAR = "bucketlist_popular";
    public static final String ACTION_ADD_BL_START = "bl_add_item_start";
    public static final String ACTION_ADD_BL_FINISH = "bl_add_item_finish";

    public static final String ACTION_PROFILE = "nav_menu:profile";
    public static final String ACTION_PROFILE_PHOTO_UPLOAD_START = "nav_menu:profile";
    public static final String ACTION_PROFILE_PHOTO_UPLOAD_FINISH = "nav_menu:profile";

    public static final String ACTION_MEMBERSHIP = "nav_menu:membership-videos";
    public static final String ACTION_MEMBERSHIP_PLAY = "member_videos_play:%s";
    public static final String ACTION_MEMBERSHIP_LOAD_START = "member_videos_download_start:%s";
    public static final String ACTION_MEMBERSHIP_LOAD_FINISHED = "member_videos_download_finish:%s";
    public static final String ACTION_MEMBERSHIP_LOAD_CANCELED = "member_videos_download_cancel:%s";

    public static final String ACTION_360 = "nav_menu:videos_360";
    public static final String ACTION_360_PLAY = "videos_360_play:%s";
    public static final String ACTION_360_LOAD_START = "videos_360_download_start:%s";
    public static final String ACTION_360_LOAD_FINISHED = "videos_360_download_finish:%s";
    public static final String ACTION_360_LOAD_CANCELED = "videos_360_download_cancel:%s";

    public static final String FIELD_MEMBER_ID = "member_id";
    public static final String TYPE = "type";
    public static final String ID = "id";

    private static List<ITracker> trackers = new ArrayList<>();

    static {
        trackers.add(new AdobeTracker());
        trackers.add(new ApptentiveTracker());
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

    private static void trackMemberAction(String action, Map<String, Object> data) {
        for (ITracker tracker : trackers) {
            tracker.trackMemberAction(action, data);
        }
    }

    private static void trackPageView(String memberId, String action) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_MEMBER_ID, memberId);
        trackMemberAction(action, data);
    }

    private static void trackSpecificPageView(String memberId, String page, String pageType, String id) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_MEMBER_ID, memberId);
        data.put(pageType, id);
        trackMemberAction(page, data);
    }


    ///////////
    /// TRACKING ACTIONS
    //////////

    public static void login(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        trackMemberAction(ACTION_LOGIN, data);
    }

    public static void dreamTrips(String memberId) {
        trackPageView(memberId, ACTION_DREAMTRIPS);
    }

    public static void trip(String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("view_trip", id);
        data.put(FIELD_MEMBER_ID, memberId);
        trackMemberAction(ACTION_DREAMTRIPS, data);
    }

    public static void tripInfo(String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("trip_info", id);
        data.put(FIELD_MEMBER_ID, memberId);
        trackMemberAction(ACTION_DREAMTRIPS, data);
    }

    public static void bookIt(String id, String memberId) {
        trackSpecificPageView(memberId, ACTION_DREAMTRIPS, "book_it", id);
    }

    public static void ysbh(String memberId) {
        trackPageView(memberId, ACTION_PHOTOS_YSBH);
    }

    public static void inspr(String memberId) {
        trackPageView(memberId, ACTION_PHOTOS_INSPR);
    }

    public static void insprDetails(String memberId, String id) {
        trackPageView(memberId, String.format(ACTION_INSPR_DETAILS, id));
    }

    public static void insprShare(String id, String type) {
        Map<String, Object> data = new HashMap<>();
        data.put(TYPE, type);
        data.put(ID, id);
        trackMemberAction(ACTION_INSPR_SHARE, data);
    }

    public static void view(TripImagesListFragment.Type type, String id, String memberId) {
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            trackSpecificPageView(memberId, ACTION_PHOTOS_YSBH, "view_ysbh_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            trackSpecificPageView(memberId, ACTION_PHOTOS_ALL_USERS, "view_user_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            trackSpecificPageView(memberId, ACTION_PHOTOS_MINE, "view_user_photo", id);
        }
    }

    public static void like(TripImagesListFragment.Type type, String id, String memberId) {
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            trackSpecificPageView(memberId, ACTION_PHOTOS_YSBH, "like_ysbh_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            trackSpecificPageView(memberId, ACTION_PHOTOS_ALL_USERS, "like_user_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            trackSpecificPageView(memberId, ACTION_PHOTOS_MINE, "like_user_photo", id);
        }
    }

    public static void flag(TripImagesListFragment.Type type, String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            data.put(FIELD_MEMBER_ID, memberId);
            data.put("flag_user_photo", id);
            trackMemberAction(ACTION_PHOTOS_ALL_USERS, data);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            data.put(FIELD_MEMBER_ID, memberId);
            data.put("flag_user_photo", id);
            trackMemberAction(ACTION_PHOTOS_MINE, data);
        }
    }

    public static void all(String memberId) {
        trackPageView(memberId, ACTION_PHOTOS_ALL_USERS);
    }

    public static void mine(String memberId) {
        trackPageView(memberId, ACTION_PHOTOS_MINE);
    }

    public static void enroll(String memberId) {
        trackPageView(memberId, ACTION_ENROLL);
    }

    public static void profile(String memberId) {
        trackPageView(memberId, ACTION_PROFILE);
    }

    public static void faq(String memberId) {
        trackPageView(memberId, ACTION_FAQ);
    }

    public static void privacy(String memberId) {
        trackPageView(memberId, ACTION_PRIVACY);
    }

    public static void cookie(String memberId) {
        trackPageView(memberId, ACTION_COOKIE);
    }

    public static void service(String memberId) {
        trackPageView(memberId, ACTION_SERVICE);
    }

    public static void photoUploadStarted(String type, String id) {
        Map<String, Object> data = new HashMap<>();
        data.put(TYPE, type);
        data.put(ID, id);
        trackMemberAction(ACTION_PHOTO_UPLOAD, data);
    }

    public static void photoUploadFinished(String type, String id) {
        Map<String, Object> data = new HashMap<>();
        data.put(TYPE, type);
        data.put(ID, id);
        trackMemberAction(ACTION_PHOTO_FINISHED, data);
    }

    public static void video360(String memberId) {
        trackPageView(memberId, ACTION_360);
    }

    public static void memberVideos(String memberId) {
        trackPageView(memberId, ACTION_MEMBERSHIP);
    }

    public static void videoAction(String memberId, String action, String videoName) {
        trackPageView(memberId, String.format(action, videoName));
    }

    public static void ota(String memberId) {
        trackPageView(memberId, ACTION_OTA);
    }

    public static void repEnroll(String memberId) {
        trackPageView(memberId, ACTION_REP_ENROLL);
    }

    public static void trainingVideos(String memberId) {
        trackPageView(memberId, ACTION_TRAINING_VIDEOS);
    }

    public static void successStories(String memberId) {
        trackPageView(memberId, ACTION_SS);
    }

    public static void viewSS(String memberId, int id) {
        trackPageView(memberId, String.format(ACTION_SS_VIEW, id));
    }

    public static void unlikeSS(String memberId, int id) {
        trackPageView(memberId, String.format(ACTION_SS_UNLIKE, id));
    }

    public static void likeSS(String memberId, int id) {
        trackPageView(memberId, String.format(ACTION_SS_LIKE, id));
    }

    public static void inviteShare(String memberId) {
        trackPageView(memberId, ACTION_INVITE);
    }

    public static void inviteShareContacts(String memberId) {
        trackPageView(memberId, ACTION_INVITE_CONTACTS);
    }

    public static void inviteShareTemplate(String memberId, int templateId) {
        trackPageView(memberId, String.format(ACTION_TEMPLATE, templateId));
    }

    public static void inviteShareAction(String action, int templateId, int count) {
        Map<String, Object> data = new HashMap<>();
        data.put("template_id", templateId);
        data.put("count", count);
        trackMemberAction(action, data);
    }

    public static void bucketList(String memberId) {
        trackPageView(memberId, ACTION_BUCKET_LIST);
    }

    public static void bucketPhotoAction(String action, String type, String blType) {
        Map<String, Object> data = new HashMap<>();
        data.put(TYPE, type);
        data.put("bl_type", blType);
        trackMemberAction(action, data);
    }

    public static void bucketItemView(String type, String id) {
        Map<String, Object> data = new HashMap<>();
        data.put(ID, id);
        data.put("bl_type", type);
        trackMemberAction(ACTION_BL_ITEM_VIEW, data);
    }

    public static void bucketPopular(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("bl_type", type);
        trackMemberAction(ACTION_BL_POPULAR, data);
    }

    public static void bucketAddStart(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("bl_type", type);
        trackMemberAction(ACTION_ADD_BL_START, data);
    }

    public static void bucketAddFinish(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("bl_type", type);
        trackMemberAction(ACTION_ADD_BL_FINISH, data);
    }

}
