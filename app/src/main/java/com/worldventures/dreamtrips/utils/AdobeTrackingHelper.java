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


    public static void login(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        Analytics.trackAction(LOGIN, data);
    }

    public static void dreamTrips() {
        Analytics.trackAction(DREAMTRIPS, null);
    }

    public static void trip(String id) {
        Map<String, Object> data = new HashMap<>();
        data.put("view_trip", id);
        Analytics.trackAction(DREAMTRIPS, data);
    }

    public static void tripInfo(String id) {
        Map<String, Object> data = new HashMap<>();
        data.put("trip_info", id);
        Analytics.trackAction(DREAMTRIPS, data);
    }

    public static void bookIt(String id) {
        Map<String, Object> data = new HashMap<>();
        data.put("book_it", id);
        Analytics.trackAction(DREAMTRIPS, data);
    }


    public static void ysbh() {
        Analytics.trackAction(PHOTOS_YSBH, null);
    }

    public static void view(TripImagesListFragment.Type type, String id) {
        Map<String, Object> data = new HashMap<>();
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            data.put("view_ysbh_photo", id);
            Analytics.trackAction(PHOTOS_YSBH, data);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            data.put("view_user_photo", id);
            Analytics.trackAction(PHOTOS_ALL_USERS, data);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            data.put("view_user_photo", id);
            Analytics.trackAction(PHOTOS_MINE, data);
        }
    }

    public static void like(TripImagesListFragment.Type type, String id) {
        Map<String, Object> data = new HashMap<>();
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            data.put("like_ysbh_photo", id);
            Analytics.trackAction(PHOTOS_YSBH, data);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            data.put("like_user_photo", id);
            Analytics.trackAction(PHOTOS_ALL_USERS, data);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            data.put("like_user_photo", id);
            Analytics.trackAction(PHOTOS_MINE, data);
        }
    }

    public static void flag(TripImagesListFragment.Type type, String id) {
        Map<String, Object> data = new HashMap<>();
        if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            data.put("flag_user_photo", id);
            Analytics.trackAction(PHOTOS_ALL_USERS, data);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            data.put("flag_user_photo", id);
            Analytics.trackAction(PHOTOS_MINE, data);
        }
    }

    public static void all() {
        Analytics.trackAction(PHOTOS_ALL_USERS, null);
    }

    public static void mine() {
        Analytics.trackAction(PHOTOS_MINE, null);
    }

    public static void video() {
        Analytics.trackAction(MEMBERSHIP, null);
    }

    public static void playVideo(String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("play_video", name);
        Analytics.trackAction(MEMBERSHIP, data);
    }

    public static void enroll() {
        Analytics.trackAction(ENROLL, null);
    }

    public static void profile() {
        Analytics.trackAction(PROFILE, null);
    }

    public static void bucketList() {
        Analytics.trackAction(BUCKET_LIST, null);
    }

    public static void faq() {
        Analytics.trackAction(FAQ, null);
    }

    public static void privacy() {
        Analytics.trackAction(PRIVACY, null);
    }

    public static void cookie() {
        Analytics.trackAction(COOKIE, null);
    }

    public static void service() {
        Analytics.trackAction(SERVICE, null);
    }
}
