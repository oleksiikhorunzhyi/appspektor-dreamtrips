package com.worldventures.dreamtrips.core.repository;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.CachedTripFilters;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.config.model.Configuration;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.TermsAndConditions;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.record.SyncRecordsStatus;

import java.util.Collection;
import java.util.List;

import io.techery.janet.smartcard.mock.device.SimpleDeviceStorage;

public interface SnappyRepository {

   String CIRCLES = "circles";
   String TRIP_FILTERS = "trip_filters";
   String CATEGORIES = "categories";
   String BUCKET_LIST = "bucket_items";
   String SETTINGS_KEY = "settings";
   String TRANSLATION = "translation";
   String POST = "post";
   String MEDIA_UPLOAD_ENTITY = "VIDEO_UPLOAD_ENTITY"; // "VIDEO_" left as is for existing user stores
   String MEDIA_UPLOAD_MODEL = "MEDIA_UPLOAD_MODEL";
   String LAST_SELECTED_VIDEO_LOCALE = "LAST_SELECTED_VIDEO_LOCALE";
   String LAST_SELECTED_VIDEO_LANGUAGE = "LAST_SELECTED_VIDEO_LANGUAGE ";
   String IMAGE = "IMAGE";
   String LAST_USED_INSPIRE_ME_RANDOM_SEED = "LAST_USED_INSPIRE_ME_RANDOM_SEED";
   String OPEN_BUCKET_TAB_TYPE = "open_bucket_tab_type";
   String BADGE_NOTIFICATIONS_COUNT = "badge_notifications_count";
   String EXCLUSIVE_NOTIFICATIONS_COUNT = "Unread-Notifications-Count";
   String FRIEND_REQUEST_COUNT = "Friend-Requests-Count";
   String GCM_REG_TOKEN = "GCM_REG_TOKEN ";
   String LAST_SYNC_APP_VERSION = "LAST_SYNC_APP_VERSION";
   String FILTER_CIRCLE = "FILTER_CIRCLE";
   String FILTER_FEED_FRIEND_FILTER_CIRCLE = "FILTER_FEED_FRIEND_FILTER_CIRCLE";
   String SOCIAL_VIEW_PAGER_STATE = "SOCIAL_VIEW_PAGER_STATE";
   String PODCASTS = "PODCASTS";
   String TRIPS = "TRIPS";
   String PINS = "PINS";
   String TRIPS_DETAILS = "TRIPS_DETAILS";

   String DTL_TRANSACTION_PREFIX = "DTL_TRANSACTION_";
   String DTL_LAST_MAP_POSITION = "DTL_LAST_MAP_POSITION";

   String FEEDBACK_TYPES = "FEEDBACK_TYPES";
   String DOCUMENTS = "DOCUMENTS";
   String SUGGESTED_PHOTOS_SYNC_TIME = "SUGGESTED_PHOTOS_SYNC_TIME";

   String CONFIGURATION = "CONFIGURATION";
   String UPDATE_APP_OPTIONAL_DIALOG_CONFIRMED_TIMESTAMP = "UPDATE_APP_OPTIONAL_DIALOG_CONFIRMED_TIMESTAMP";

   String NOTIFICATIONS = "notifications";
   String UNDEFINED_FEED_ITEM = "undefined";
   String PHOTO_FEED_ITEM = "photo";
   String POST_FEED_ITEM = "post";
   String TRIP_FEED_ITEM = "trip";
   String BUCKET_FEED_ITEM = "bucket";

   String WALLET_SMART_CARD = "WALLET_SMART_CARD";
   String WALLET_SMART_CARD_USER = "WALLET_SMART_CARD_USER";
   String WALLET_DETAILS_SMART_CARD = "WALLET_DETAILS_SMART_CARD";
   String WALLET_SMART_CARD_FIRMWARE = "WALLET_SMART_CARD_FIRMWARE";
   String WALLET_DEVICE_STORAGE = "WALLET_DEVICE_STORAGE";
   String WALLET_TERMS_AND_CONDITIONS = "WALLET_TERMS_AND_CONDITIONS";
   String WALLET_FIRMWARE = "WALLET_FIRMWARE";
   String WALLET_SMART_CARD_LOCATION = "WALLET_SMART_CARD_LOCATION";
   String WALLET_LOST_SMART_CARD_ENABLE_TRAKING = "WALLET_LOST_SMART_CARD_ENABLE_TRAKING";
   String WALLET_SYNC_RECORD_STATUS = "WALLET_SYNC_RECORD_STATUS";
   String WALLET_OPTIONAL_PIN = "WALLET_OPTIONAL_PIN";

   void clearAll();

   Boolean isEmpty(String key);

   <T> void putList(String key, Collection<T> list);

   <T> List<T> readList(String key, Class<T> clazz);

   void clearAllForKey(String key);

   void clearAllForKeys(String... keys);

   void saveBucketList(List<BucketItem> items, int userId);

   List<BucketItem> readBucketList(int userId);

   void saveOpenBucketTabType(String type);

   String getOpenBucketTabType();

   void saveSettings(List<Setting> settingsList, boolean withClear);

   List<Setting> getSettings();

   void clearSettings(DB snappyDb) throws SnappydbException;

   void saveLastSuggestedPhotosSyncTime(long time);

   long getLastSuggestedPhotosSyncTime();

   void saveAppUpdateRequirement(Configuration updateRequirement);

   Configuration getAppUpdateRequirement();

   void saveAppUpdateOptionalDialogConfirmedTimestamp(long appUpdateDialogShownTimestamp);

   long getAppUpdateOptionalDialogConfirmedTimestamp();

   void savePhotoEntityList(TripImagesType type, int userId, List<IFullScreenObject> items);

   List<IFullScreenObject> readPhotoEntityList(TripImagesType type, int userId);

   void saveLastUsedInspireMeRandomSeed(double randomSeed);

   double getLastUsedInspireMeRandomSeed();

   void saveLastSelectedVideoLocale(VideoLocale videoLocale);

   VideoLocale getLastSelectedVideoLocale();

   void saveLastSelectedVideoLanguage(VideoLanguage videoLocale);

   VideoLanguage getLastSelectedVideoLanguage();

   void saveBadgeNotificationsCount(int notificationsCount);

   int getBadgeNotificationsCount();

   void saveCountFromHeader(String headerKey, int count);

   void saveNotificationsCount(int count);

   void saveFriendRequestsCount(int count);

   int getExclusiveNotificationsCount();

   int getFriendsRequestsCount();

   void saveTranslation(String originalText, String translation, String toLanguage);

   String getTranslation(String originalText, String toLanguage);

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

   void setFeedbackTypes(List<FeedbackType> types);

   List<Document> getDocuments(String type);

   void setDocuments(String type, List<Document> documents);

   void cleanLastMapCameraPosition();

   DtlTransaction getDtlTransaction(String id);

   void saveDtlTransaction(String id, DtlTransaction dtlTransaction);

   void deleteDtlTransaction(String id);

   void saveDownloadMediaModel(CachedModel e);

   List<CachedModel> getDownloadMediaModels();

   @Deprecated
   List<CachedEntity> getDownloadMediaEntities();

   @Deprecated
   void deleteAllMediaEntities();

   CachedModel getDownloadMediaModel(String id);

   void setLastSyncAppVersion(String appVersion);

   String getLastSyncAppVersion();

   void saveNotifications(List<FeedItem> notifications);

   List<FeedItem> getNotifications();

   void savePodcasts(List<Podcast> podcasts);

   List<Podcast> getPodcasts();

   void saveTrips(List<TripModel> trips);

   List<TripModel> getTrips();

   void saveTripFilters(CachedTripFilters tripFilters);

   CachedTripFilters getTripFilters();

   void savePins(List<Pin> pins);

   List<Pin> getPins();

   void saveTripDetails(TripModel tripModel);

   void saveTripsDetails(List<TripModel> trips);

   boolean hasTripsDetailsForUids(List<String> uids);

   List<TripModel> getTripsDetailsForUids(List<String> uids);

   TripModel getTripDetail(String uid);

   SimpleDeviceStorage getWalletDeviceStorage();

   void saveWalletDeviceStorage(SimpleDeviceStorage deviceStorage);

   void saveSmartCard(SmartCard smartCard);

   SmartCard getSmartCard();

   void deleteSmartCard();

   void saveSmartCardUser(SmartCardUser smartCardUser);

   SmartCardUser getSmartCardUser();

   void deleteSmartCardUser();

   void saveWalletTermsAndConditions(TermsAndConditions data);

   TermsAndConditions getWalletTermsAndConditions();

   void deleteTermsAndConditions();

   void saveSmartCardDetails(SmartCardDetails details);

   SmartCardDetails getSmartCardDetails();

   void deleteSmartCardDetails();

   void saveSmartCardFirmware(SmartCardFirmware smartCardFirmware);

   SmartCardFirmware getSmartCardFirmware();

   void deleteSmartCardFirmware();

   void saveFirmwareUpdateData(FirmwareUpdateData firmwareUpdateData);

   FirmwareUpdateData getFirmwareUpdateData();

   void deleteFirmwareUpdateData();

   void saveWalletLocations(List<WalletLocation> walletLocations);

   List<WalletLocation> getWalletLocations();

   void deleteWalletLocations();

   void saveEnabledTracking(boolean enable);

   boolean isEnableTracking();

   void saveSyncRecordsStatus(SyncRecordsStatus data);

   SyncRecordsStatus getSyncRecordsStatus();

   void saveShouldAskForPin(boolean shouldAsk);

   boolean shouldAskForPin();

   void deletePinOptionChoice();
}
