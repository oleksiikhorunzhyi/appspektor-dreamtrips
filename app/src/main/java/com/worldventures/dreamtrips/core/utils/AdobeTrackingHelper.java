package com.worldventures.dreamtrips.core.utils;

import com.adobe.mobile.Analytics;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.HashMap;
import java.util.Map;

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
        trackMemberAction(LOGIN, data);
    }

    public static void dreamTrips(String memberId) {
        trackPageView(memberId, DREAMTRIPS);
    }

    public static void trip(String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("view_trip", id);
        data.put("member_id", memberId);
        trackMemberAction(DREAMTRIPS, data);
    }

    public static void tripInfo(String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        data.put("trip_info", id);
        data.put("member_id", memberId);
        trackMemberAction(DREAMTRIPS, data);
    }

    public static void bookIt(String id, String memberId) {
        trackSpecificPageView(memberId, DREAMTRIPS, "book_it", id);
    }


    public static void ysbh(String memberId) {
        trackPageView(memberId, PHOTOS_YSBH);
    }

    public static void view(TripImagesListFragment.Type type, String id, String memberId) {
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            trackSpecificPageView(memberId, PHOTOS_YSBH, "view_ysbh_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            trackSpecificPageView(memberId, PHOTOS_ALL_USERS, "view_user_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            trackSpecificPageView(memberId, PHOTOS_MINE, "view_user_photo", id);
        }
    }

    public static void like(TripImagesListFragment.Type type, String id, String memberId) {
        if (type.equals(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE)) {
            trackSpecificPageView(memberId, PHOTOS_YSBH, "like_ysbh_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            trackSpecificPageView(memberId, PHOTOS_ALL_USERS, "like_user_photo", id);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            trackSpecificPageView(memberId, PHOTOS_MINE, "like_user_photo", id);
        }
    }

    private static void trackSpecificPageView(String memberId, String page, String pageType, String id) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        data.put(pageType, id);
        trackMemberAction(page, data);
    }

    public static void flag(TripImagesListFragment.Type type, String id, String memberId) {
        Map<String, Object> data = new HashMap<>();
        if (type.equals(TripImagesListFragment.Type.MEMBER_IMAGES)) {
            data.put("member_id", memberId);
            data.put("flag_user_photo", id);
            trackMemberAction(PHOTOS_ALL_USERS, data);
        } else if (type.equals(TripImagesListFragment.Type.MY_IMAGES)) {
            data.put("member_id", memberId);
            data.put("flag_user_photo", id);
            trackMemberAction(PHOTOS_MINE, data);
        }
    }

    public static void all(String memberId) {
        trackPageView(memberId, PHOTOS_ALL_USERS);
    }

    public static void mine(String memberId) {
        trackPageView(memberId, PHOTOS_MINE);
    }

    public static void onMemberShipVideos(String memberId) {
        trackPageView(memberId, MEMBERSHIP);
    }

    public static void playVideo(String name, String memberId) {
        final String action = MEMBERSHIP;
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        data.put("play_video", name);
        trackMemberAction(action, data);
    }

    private static void trackMemberAction(String action, Map<String, Object> data) {
        Analytics.trackAction(action, data);
    }

    public static void enroll(String memberId) {
        trackPageView(memberId, ENROLL);
    }

    public static void profile(String memberId) {
        trackPageView(memberId, PROFILE);
    }

    public static void bucketList(String memberId) {
        trackPageView(memberId, BUCKET_LIST);
    }

    public static void faq(String memberId) {
        trackPageView(memberId, FAQ);
    }

    private static void trackPageView(String memberId, String action) {
        Map<String, Object> data = new HashMap<>();
        data.put("member_id", memberId);
        trackMemberAction(action, data);
    }

    public static void privacy(String memberId) {
        trackPageView(memberId, PRIVACY);
    }

    public static void cookie(String memberId) {
        trackPageView(memberId, COOKIE);
    }

    public static void service(String memberId) {
        trackPageView(memberId, SERVICE);
    }
}
