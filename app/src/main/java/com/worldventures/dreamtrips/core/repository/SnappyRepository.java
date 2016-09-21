package com.worldventures.dreamtrips.core.repository;

import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.TermsAndConditions;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

import java.util.Collection;
import java.util.List;

import io.techery.janet.smartcard.mock.device.SimpleDeviceStorage;

public interface SnappyRepository {

   String CIRCLES = "circles";
   String REGIONS = "regions_new";
   String CATEGORIES = "categories";
   String ACTIVITIES = "activities_new";
   String BUCKET_LIST = "bucket_items";
   String SETTINGS_KEY = "settings";
   String TRANSLATION = "translation";
   String POST = "post";
   String MEDIA_UPLOAD_ENTITY = "VIDEO_UPLOAD_ENTITY"; // "VIDEO_" left as is for existing user stores
   String LAST_SELECTED_VIDEO_LOCALE = "LAST_SELECTED_VIDEO_LOCALE";
   String LAST_SELECTED_VIDEO_LANGUAGE = "LAST_SELECTED_VIDEO_LANGUAGE ";
   String IMAGE = "IMAGE";
   String OPEN_BUCKET_TAB_TYPE = "open_bucket_tab_type";
   String BADGE_NOTIFICATIONS_COUNT = "badge_notifications_count";
   String EXCLUSIVE_NOTIFICATIONS_COUNT = "Unread-Notifications-Count"; // WARNING must be equal to server header
   String FRIEND_REQUEST_COUNT = "Friend-Requests-Count"; // WARNING must be equal to server header
   String GCM_REG_TOKEN = "GCM_REG_TOKEN ";
   String LAST_SYNC_APP_VERSION = "LAST_SYNC_APP_VERSION";
   String FILTER_CIRCLE = "FILTER_CIRCLE";
   String FILTER_FEED_FRIEND_FILTER_CIRCLE = "FILTER_FEED_FRIEND_FILTER_CIRCLE";
   String SOCIAL_VIEW_PAGER_STATE = "SOCIAL_VIEW_PAGER_STATE";
   String PODCASTS = "PODCASTS";
   String TRIPS = "TRIPS";
   String PINS = "PINS";
   String TRIPS_DETAILS = "TRIPS_DETAILS";

   String DTL_MERCHANTS = "DTL_MERCHANTS";
   String DTL_SELECTED_LOCATION = "DTL_SELECTED_LOCATION";
   String DTL_TRANSACTION_PREFIX = "DTL_TRANSACTION_";
   String DTL_LAST_MAP_POSITION = "DTL_LAST_MAP_POSITION";
   String DTL_SHOW_OFFERS_ONLY_TOGGLE = "DTL_SHOW_OFFERS_ONLY_TOGGLE";
   String DTL_AMENITIES = "DTL_AMENITIES";
   String FEEDBACK_TYPES = "FEEDBACK_TYPES";
   String SUGGESTED_PHOTOS_SYNC_TIME = "SUGGESTED_PHOTOS_SYNC_TIME";

   String NOTIFICATIONS = "notifications";
   String UNDEFINED_FEED_ITEM = "undefined";
   String PHOTO_FEED_ITEM = "photo";
   String POST_FEED_ITEM = "post";
   String TRIP_FEED_ITEM = "trip";
   String BUCKET_FEED_ITEM = "bucket";

   String WALLET_CARDS_LIST = "WALLET_CARDS_LIST";
   String WALLET_SMART_CARD = "WALLET_SMART_CARD";
   String WALLET_DETAILS_SMART_CARD = "WALLET_DETAILS_SMART_CARD";
   String WALLET_ACTIVE_SMART_CARD_ID = "WALLET_ACTIVE_SMART_CARD_ID";
   String WALLET_DEVICE_STORAGE = "WALLET_DEVICE_STORAGE";
   String WALLET_DEFAULT_BANK_CARD = "WALLET_DEFAULT_BANK_CARD";
   String WALLET_DEFAULT_ADDRESS = "WALLET_DEFAULT_ADDRESS";
   String WALLET_TERMS_AND_CONDITIONS = "WALLET_TERMS_AND_CONDITIONS";

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

   void savePhotoEntityList(TripImagesType type, int userId, List<IFullScreenObject> items);

   List<IFullScreenObject> readPhotoEntityList(TripImagesType type, int userId);

   void saveLastSelectedVideoLocale(VideoLocale videoLocale);

   VideoLocale getLastSelectedVideoLocale();

   void saveLastSelectedVideoLanguage(VideoLanguage videoLocale);

   VideoLanguage getLastSelectedVideoLanguage();

   void saveBadgeNotificationsCount(int notificationsCount);

   int getBadgeNotificationsCount();

   void saveCountFromHeader(String headerKey, int count);

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

   void saveDownloadMediaEntity(CachedEntity e);

   List<CachedEntity> getDownloadMediaEntities();

   CachedEntity getDownloadMediaEntity(String id);

   void setLastSyncAppVersion(String appVersion);

   String getLastSyncAppVersion();

   void saveNotifications(List<FeedItem> notifications);

   List<FeedItem> getNotifications();

   void savePodcasts(List<Podcast> podcasts);

   List<Podcast> getPodcasts();

   void saveTrips(List<TripModel> trips);

   List<TripModel> getTrips();

   void savePins(List<Pin> pins);

   List<Pin> getPins();

   void saveTripDetails(TripModel tripModel);

   void saveTripsDetails(List<TripModel> trips);

   List<TripModel> getTripsDetailsForUids(List<String> uids);

   TripModel getTripDetail(String uid);

   SimpleDeviceStorage getWalletDeviceStorage();

   void saveWalletDeviceStorage(SimpleDeviceStorage deviceStorage);

   void saveWalletCardsList(List<Card> items);

   List<Card> readWalletCardsList();

   void saveWalletDefaultCardId(String id);

   String readWalletDefaultCardId();

   Card readDefaultCard();

   void saveDefaultAddress(AddressInfo addressInfo);

   AddressInfo readDefaultAddress();

   void saveSmartCard(SmartCard smartCard);

   SmartCard getSmartCard(String smartCardId);

   List<SmartCard> getSmartCards();

   String getActiveSmartCardId();

   void setActiveSmartCardId(String scid);

   void saveWalletTermsAndConditions(TermsAndConditions data);

   TermsAndConditions getWalletTermsAndConditions();

   void saveSmartCardDetails(SmartCardDetails details);

   SmartCardDetails getSmartCardDetails(String smartCardId);
}
