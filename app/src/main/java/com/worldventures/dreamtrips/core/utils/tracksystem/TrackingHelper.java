package com.worldventures.dreamtrips.core.utils.tracksystem;

import android.app.Activity;

import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackingHelper {

    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_DREAMTRIPS = "nav_menu:dreamtrips";
    private static final String ACTION_PHOTOS_YSBH = "nav_menu:photos-ysbh";
    private static final String ACTION_PHOTOS_ALL_USERS = "nav_menu:photos-allusers";
    private static final String ACTION_PHOTOS_MINE = "nav_menu:photos-mine";
    private static final String ACTION_MEMBERSHIP = "nav_menu:membership-videos";
    private static final String ACTION_ENROLL = "nav_menu:membership-enroll";
    private static final String ACTION_BUCKET_LIST = "nav_menu:bucketlist";
    private static final String ACTION_PROFILE = "nav_menu:profile";
    private static final String ACTION_FAQ = "nav_menu:faq";
    private static final String ACTION_PRIVACY = "nav_menu:terms-privacy";
    private static final String ACTION_COOKIE = "nav_menu:terms-cookie";
    private static final String ACTION_SERVICE = "nav_menu:terms-service";
    private static final String ACTION_PHOTO_UPLOAD = "photo_upload";
    private static final String ACTION_PHOTOS_INSPIRE_ME = "nav_menu:photos-inspireme";
    

    private static final String FIELD_MEMBER_ID = "member_id";

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

    private static void trackSpecificPageView(String memberId, String page, String pageType, String id) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_MEMBER_ID, memberId);
        data.put(pageType, id);
        trackMemberAction(page, data);
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

    public static void onMemberShipVideos(String memberId) {
        trackPageView(memberId, ACTION_MEMBERSHIP);
    }

    public static void playVideo(String name, String memberId) {
        final String action = ACTION_MEMBERSHIP;
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_MEMBER_ID, memberId);
        data.put("play_video", name);
        trackMemberAction(action, data);
    }

    public static void enroll(String memberId) {
        trackPageView(memberId, ACTION_ENROLL);
    }

    public static void profile(String memberId) {
        trackPageView(memberId, ACTION_PROFILE);
    }

    public static void bucketList(String memberId) {
        trackPageView(memberId, ACTION_BUCKET_LIST);
    }

    public static void faq(String memberId) {
        trackPageView(memberId, ACTION_FAQ);
    }

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

    public static void privacy(String memberId) {
        trackPageView(memberId, ACTION_PRIVACY);
    }

    public static void cookie(String memberId) {
        trackPageView(memberId, ACTION_COOKIE);
    }

    public static void service(String memberId) {
        trackPageView(memberId, ACTION_SERVICE);
    }


}
