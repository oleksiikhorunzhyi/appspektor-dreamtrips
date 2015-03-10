package com.worldventures.dreamtrips.utils;

import com.adobe.mobile.Analytics;
import com.worldventures.dreamtrips.presentation.tripimages.YSBHPM;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 1 on 09.02.15.
 */
public class AdobeTrackingHelper {

    private static final String LOGIN = "login";
    private static final String DREAMTRIPS = "nav_menu:dreamtrips";
    private static final String PHOTOS_YSBH = "nav_menu:photos-ysbh";
    private static final String PHOTOS_ALL_USERS = "nav_menu:photos-allusers";
    private static final String PHOTOS_MINE = "nav_menu:photos-mine";
    private static final String MEMBERSHIP = "nav_menu:membership-videos";
    private static final String ENROLL = "nav_menu:membership-enroll";
    private static final String BUCKET_LIST = "nav_menu:bucketlist";
    private static final String PROFILE = "nav_menu:profile";
    private static final String FAQ = "nav_menu:faq";
    private static final String PRIVACY = "nav_menu:terms-privacy";
    private static final String COOKIE = "nav_menu:terms-cookie";
    private static final String SERVICE = "nav_menu:terms-service";


    public static void login(String userId, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("member_id", memberId);
        Analytics.trackAction(LOGIN, data);
    }

    public static void dreamTrips(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(DREAMTRIPS, data);
    }

    public static void trip(String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("view_trip", id);
        data.put("member_id", memberId);
        Analytics.trackAction(DREAMTRIPS, data);
    }

    public static void tripInfo(String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("trip_info", id);
        data.put("member_id", memberId);
        Analytics.trackAction(DREAMTRIPS, data);
    }

    public static void bookIt(String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        data.put("book_it", id);
        Analytics.trackAction(DREAMTRIPS, data);
    }


    public static void ysbh(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(PHOTOS_YSBH, data);
    }

    public static void view(TripImagesListFragment.Type type, String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            data.put("member_id", memberId);
            data.put("view_ysbh_photo", id);
            Analytics.trackAction(PHOTOS_YSBH, data);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            data.put("member_id", memberId);
            data.put("view_user_photo", id);
            Analytics.trackAction(PHOTOS_ALL_USERS, data);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            data.put("member_id", memberId);
            data.put("view_user_photo", id);
            Analytics.trackAction(PHOTOS_MINE, data);
        }
    }

    public static void like(TripImagesListFragment.Type type, String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            data.put("member_id", memberId);
            data.put("like_ysbh_photo", id);
            Analytics.trackAction(PHOTOS_YSBH, data);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            data.put("member_id", memberId);
            data.put("like_user_photo", id);
            Analytics.trackAction(PHOTOS_ALL_USERS, data);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            data.put("member_id", memberId);
            data.put("like_user_photo", id);
            Analytics.trackAction(PHOTOS_MINE, data);
        }
    }

    public static void flag(TripImagesListFragment.Type type, String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            data.put("member_id", memberId);
            data.put("flag_user_photo", id);
            Analytics.trackAction(PHOTOS_ALL_USERS, data);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            data.put("member_id", memberId);
            data.put("flag_user_photo", id);
            Analytics.trackAction(PHOTOS_MINE, data);
        }
    }

    public static void all(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(PHOTOS_ALL_USERS, data);
    }

    public static void mine(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(PHOTOS_MINE, data);
    }

    public static void video(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(MEMBERSHIP, data);
    }

    public static void playVideo(String name, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        data.put("play_video", name);
        Analytics.trackAction(MEMBERSHIP, data);
    }

    public static void enroll(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(ENROLL, data);
    }

    public static void profile(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(PROFILE, data);
    }

    public static void bucketList(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(BUCKET_LIST, data);
    }

    public static void faq(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(FAQ, data);
    }

    public static void privacy(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(PRIVACY, data);
    }

    public static void cookie(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(COOKIE, data);
    }

    public static void service(String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        Analytics.trackAction(SERVICE, data);
    }
}
