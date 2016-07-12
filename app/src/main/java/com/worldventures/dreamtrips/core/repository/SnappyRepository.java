package com.worldventures.dreamtrips.core.repository;

import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoCreationItem;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface SnappyRepository {
    String CIRCLES = "circles";
    String REGIONS = "regions_new";
    String CATEGORIES = "categories";
    String ACTIVITIES = "activities_new";
    String BUCKET_LIST = "bucket_items";
    String TRIP_KEY = "trip_rezopia_v2";
    String SETTINGS_KEY = "settings";
    String POST = "post";
    String UPLOAD_TASK_KEY = "amazon_upload_task";
    String BUCKET_PHOTO_CREATION_ITEM = "BUCKET_PHOTO_CREATION_ITEM";
    String MEDIA_UPLOAD_ENTITY = "VIDEO_UPLOAD_ENTITY"; // "VIDEO_" left as is for existing user stores
    String INVITE_MEMBER = "INVITE_MEMBER ";
    String LAST_SELECTED_VIDEO_LOCALE = "LAST_SELECTED_VIDEO_LOCALE";
    String LAST_SELECTED_VIDEO_LANGUAGE = "LAST_SELECTED_VIDEO_LANGUAGE ";
    String IMAGE = "IMAGE";
    String RECENT_BUCKET_COUNT = "recent_bucket_items_count";
    String OPEN_BUCKET_TAB_TYPE = "open_bucket_tab_type";
    String BADGE_NOTIFICATIONS_COUNT = "badge_notifications_count";
    String EXCLUSIVE_NOTIFICATIONS_COUNT = "Unread-Notifications-Count"; // WARNING must be equal to server header
    String FRIEND_REQUEST_COUNT = "Friend-Requests-Count"; // WARNING must be equal to server header
    String GCM_REG_TOKEN = "GCM_REG_TOKEN ";
    String GCM_REG_ID_PERSISTED = "GCM_REG_ID_PERSISTED ";
    String FILTER_CIRCLE = "FILTER_CIRCLE";
    String FILTER_FEED_FRIEND_FILTER_CIRCLE = "FILTER_FEED_FRIEND_FILTER_CIRCLE";
    String SOCIAL_VIEW_PAGER_STATE = "SOCIAL_VIEW_PAGER_STATE";

    String DTL_MERCHANTS = "DTL_MERCHANTS";
    String DTL_SELECTED_LOCATION = "DTL_SELECTED_LOCATION";
    String DTL_TRANSACTION_PREFIX = "DTL_TRANSACTION_";
    String DTL_LAST_MAP_POSITION = "DTL_LAST_MAP_POSITION";
    String DTL_SHOW_OFFERS_ONLY_TOGGLE = "DTL_SHOW_OFFERS_ONLY_TOGGLE";
    String DTL_AMENITIES = "DTL_AMENITIES";
    String FEEDBACK_TYPES = "FEEDBACK_TYPES";
    String SUGGESTED_PHOTOS_SYNC_TIME = "SUGGESTED_PHOTOS_SYNC_TIME";


    void clearAll();

    Boolean isEmpty(String key);

    <T> void putList(String key, Collection<T> list);

    <T> List<T> readList(String key, Class<T> clazz);

    void clearAllForKey(String key);

    void clearAllForKeys(String... keys);

    void saveBucketList(List<BucketItem> items, String type);

    void saveBucketList(List<BucketItem> items, String type, int userId);

    List<BucketItem> readBucketList(String type, int userId);

    int getRecentlyAddedBucketItems(String type);

    void saveRecentlyAddedBucketItems(String type, int count);

    void saveOpenBucketTabType(String type);

    String getOpenBucketTabType();

    void saveTrip(TripModel trip);

    void saveTrips(List<TripModel> list);

    List<TripModel> getTrips();

    void clearTrips(DB snappyDb) throws SnappydbException;

    void saveDownloadMediaEntity(CachedEntity e);

    CachedEntity getDownloadMediaEntity(String id);

    void saveSettings(List<Setting> settingsList, boolean withClear);

    List<Setting> getSettings();

    void clearSettings(DB snappyDb) throws SnappydbException;

    void saveLastSuggestedPhotosSyncTime(long time);

    long getLastSuggestedPhotosSyncTime();

    void saveUploadTask(UploadTask uploadTask);

    UploadTask getUploadTask(String filePath);

    void removeUploadTask(UploadTask uploadTask);

    void removeAllUploadTasks();

    void saveBucketPhotoCreationItem(BucketPhotoCreationItem uploadTask);

    BucketPhotoCreationItem getBucketPhotoCreationItem(String filePath);

    void removeBucketPhotoCreationItem(BucketPhotoCreationItem uploadTask);

    void removeAllBucketItemPhotoCreations();

    List<BucketPhotoCreationItem> getAllBucketPhotoCreationItem();

    List<UploadTask> getAllUploadTask();

    void savePhotoEntityList(TripImagesType type, int userId, List<IFullScreenObject> items);

    List<IFullScreenObject> readPhotoEntityList(TripImagesType type, int userId);

    void addInviteMember(Member member);

    List<Member> getInviteMembers();

    void saveLastSelectedVideoLocale(VideoLocale videoLocale);

    VideoLocale getLastSelectedVideoLocale();

    void saveLastSelectedVideoLanguage(VideoLanguage videoLocale);

    VideoLanguage getLastSelectedVideoLanguage();

    void saveBadgeNotificationsCount(int notificationsCount);

    int getBadgeNotificationsCount();

    void saveCountFromHeader(String headerKey, int count);

    int getExclusiveNotificationsCount();

    int getFriendsRequestsCount();

    void saveCircles(List<Circle> circles);

    List<Circle> getCircles();

    void saveFilterCircle(Circle circle);

    Circle getFilterCircle();

    Circle getFeedFriendPickedCircle();

    void saveFeedFriendPickedCircle(Circle circle);

    String getGcmRegToken();

    void setGcmRegToken(String token);

    void saveSocialViewPagerState(SocialViewPagerState state);

    SocialViewPagerState getSocialViewPagerState();

    List<FeedbackType> getFeedbackTypes();

    void setFeedbackTypes(ArrayList<FeedbackType> types);

    void saveDtlLocation(DtlLocation dtlLocation);

    void cleanDtlLocation();

    @Nullable
    DtlLocation getDtlLocation();

    void saveDtlMerhants(List<DtlMerchant> merchants);

    List<DtlMerchant> getDtlMerchants();

    void saveAmenities(Collection<DtlMerchantAttribute> amenities);

    List<DtlMerchantAttribute> getAmenities();

    void clearMerchantData();

    void saveLastMapCameraPosition(Location location);

    Location getLastMapCameraPosition();

    void cleanLastMapCameraPosition();

    void saveLastSelectedOffersOnlyToogle(boolean state);

    Boolean getLastSelectedOffersOnlyToggle();

    void cleanLastSelectedOffersOnlyToggle();

    DtlTransaction getDtlTransaction(String id);

    void saveDtlTransaction(String id, DtlTransaction dtlTransaction);

    void deleteDtlTransaction(String id);
}