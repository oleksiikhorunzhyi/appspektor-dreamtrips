package com.worldventures.dreamtrips.core.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.innahema.collections.query.queriables.Queryable;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.UndefinedFeedItem;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.settings.model.FlagSetting;
import com.worldventures.dreamtrips.modules.settings.model.SelectSetting;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableTermsAndConditions;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.TermsAndConditions;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.Crypter.CryptoData;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.HybridAndroidCrypter;

import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.techery.janet.smartcard.mock.device.SimpleDeviceStorage;
import timber.log.Timber;

public class SnappyRepositoryImpl implements SnappyRepository {

   private Context context;
   private ExecutorService executorService;
   private Kryo kryo;
   private HybridAndroidCrypter crypter;

   public SnappyRepositoryImpl(@NonNull Context context, @NonNull HybridAndroidCrypter crypter) {
      this.context = context;
      this.crypter = crypter;
      this.executorService = Executors.newSingleThreadExecutor();
      //
      initCustomKryo();
   }

   private void initCustomKryo() {
      this.kryo = new Kryo();
      this.kryo.register(Date.class, new DefaultSerializers.DateSerializer());
      this.kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
      Kryo.DefaultInstantiatorStrategy strategy = new Kryo.DefaultInstantiatorStrategy();
      strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
      this.kryo.setInstantiatorStrategy(strategy);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Proxy helpers
   ///////////////////////////////////////////////////////////////////////////

   private void act(SnappyAction action) {
      executorService.execute(() -> {
         DB snappyDb = null;
         try {
            snappyDb = DBFactory.open(context);
            action.call(snappyDb);
         } catch (SnappydbException e) {
            if (isNotFound(e)) Timber.v("Nothing found");
            else Timber.w(e, "DB fails to act", e);
         } finally {
            try {
               if (snappyDb != null && snappyDb.isOpen()) snappyDb.close();
            } catch (SnappydbException e) {
               Timber.w(e, "DB fails to close");
            }
         }
      });
   }

   private <T> Optional<T> actWithResult(SnappyResult<T> action) {
      Future<T> future = executorService.submit(() -> {
         DB snappyDb = null;
         try {
            snappyDb = DBFactory.open(context);
            T result = action.call(snappyDb);
            Timber.v("DB action result: %s", result);
            return result;
         } catch (SnappydbException e) {
            if (isNotFound(e)) Timber.v("Nothing found");
            else Timber.w(e, "DB fails to act with result", e);
            return null;
         } finally {
            if (snappyDb != null) try {
               snappyDb.close();
            } catch (SnappydbException e) {
               Timber.w(e, "DB fails to close");
            }
         }
      });
      try {
         return Optional.fromNullable(future.get());
      } catch (InterruptedException | ExecutionException e) {
         Timber.w(e, "DB fails to act with result");
         return Optional.absent();
      }
   }

   private boolean isNotFound(SnappydbException e) {
      return e.getMessage().contains("NotFound");
   }

   @Override
   public void clearAll() {
      act(DB::destroy);
   }

   @Override
   public Boolean isEmpty(String key) {
      return actWithResult((db) -> {
         String[] keys = db.findKeys(key);
         return keys == null || keys.length == 0;
      }).or(false);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Public
   ///////////////////////////////////////////////////////////////////////////

   private void putEncrypted(String key, Object obj) {
      act(db -> {
         Output output = new Output(new ByteArrayOutputStream());
         kryo.writeClassAndObject(output, obj);
         byte[] bytes = crypter.encrypt(new CryptoData(new ByteArrayInputStream(output.getBuffer()))).toByteArray();
         db.put(key, bytes);
      });
   }

   private <T> T getEncrypted(String key, Class<T> clazz) {
      return actWithResult(db -> {
         T result = null;
         Input input = new Input();
         try {
            byte[] bytes = crypter.decrypt(new CryptoData(new ByteArrayInputStream(db.getBytes(key)))).toByteArray();
            input.setBuffer(bytes);
            result = (T) kryo.readClassAndObject(input);
         } finally {
            input.close();
         }
         return result;
      }).orNull();
   }

   private <T> List<T> getEncryptedList(String key) {
      List decrypted = getEncrypted(key, List.class);
      if (decrypted == null) return Collections.emptyList();
      else return (List<T>) decrypted;
   }

   @Override
   public <T> void putList(String key, Collection<T> list) {
      act(db -> {
         Output output = new Output(new ByteArrayOutputStream());
         kryo.writeClassAndObject(output, list);
         db.put(key, output.getBuffer());
      });
   }

   @Override
   public <T> List<T> readList(String key, Class<T> clazz) {
      return actWithResult(db -> {
         Collection<T> result;
         Input input = new Input();
         try {
            input.setBuffer(db.getBytes(key));
            result = (Collection<T>) kryo.readClassAndObject(input);
         } finally {
            input.close();
         }
         return new ArrayList(result);
      }).or(new ArrayList<>());
   }

   /**
    * Method is intended to delete all records for given key.
    *
    * @param key key to be deleted.
    */
   @Override
   public void clearAllForKey(String key) {
      clearAllForKeys(key);
   }

   /**
    * Method is intended to delete all records for given keys.
    *
    * @param keys keys array to be deleted.
    */
   @Override
   public void clearAllForKeys(String... keys) {
      Queryable.from(keys).forEachR(key -> act(db -> {
         String[] placesKeys = db.findKeys(key);
         for (String placeKey : placesKeys) {
            db.del(placeKey);
         }
      }));
   }

   ///////////////////////////////////////////////////////////////////////////
   // BucketItems
   ///////////////////////////////////////////////////////////////////////////
   @Override
   public void saveBucketList(List<BucketItem> items, int userId) {
      putList(BUCKET_LIST + "_" + userId, items);
   }

   @Override
   public List<BucketItem> readBucketList(int userId) {
      return readBucketList(BUCKET_LIST + "_" + userId);
   }

   private List<BucketItem> readBucketList(String key) {
      List<BucketItem> list = readList(key, BucketItem.class);
      Collections.sort(list, (lhs, rhs) -> {
         if (lhs.isDone() == rhs.isDone()) return 0;
         else if (lhs.isDone() && !rhs.isDone()) return 1;
         else return -1;
      });
      return list;
   }

   @Override
   public void saveOpenBucketTabType(String type) {
      act(db -> db.put(OPEN_BUCKET_TAB_TYPE, type));
   }

   @Override
   public String getOpenBucketTabType() {
      return actWithResult(db -> db.get(OPEN_BUCKET_TAB_TYPE)).orNull();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Media
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public List<CachedEntity> getDownloadMediaEntities() {
      return actWithResult(db -> {
         List<CachedEntity> entities = new ArrayList<>();
         String[] keys = db.findKeys(MEDIA_UPLOAD_ENTITY);
         for (String key : keys) {
            entities.add(db.get(key, CachedEntity.class));
         }
         return entities;
      }).or(Collections.emptyList());
   }

   @Override
   public void saveDownloadMediaEntity(CachedEntity e) {
      act(db -> db.put(MEDIA_UPLOAD_ENTITY + e.getUuid(), e));
   }

   @Override
   public CachedEntity getDownloadMediaEntity(String id) {
      return actWithResult(db -> db.get(MEDIA_UPLOAD_ENTITY + id, CachedEntity.class)).orNull();
   }

   @Override
   public String getLastSyncAppVersion() {
      return actWithResult(db -> db.get(LAST_SYNC_APP_VERSION)).orNull();
   }

   @Override
   public void setLastSyncAppVersion(String appVersion) {
      act(db -> db.put(LAST_SYNC_APP_VERSION, appVersion));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Wallet
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveSmartCard(SmartCard smartCard) {
      putEncrypted(WALLET_SMART_CARD + smartCard.smartCardId(), smartCard);
   }

   @Override
   public SmartCard getSmartCard(String smartCardId) {
      return getEncrypted(WALLET_SMART_CARD + smartCardId, ImmutableSmartCard.class);
   }

   @Override
   public List<SmartCard> getSmartCards() {
      List<SmartCard> result = new ArrayList<>();
      String[] keys = actWithResult(db -> db.findKeys(WALLET_SMART_CARD)).or(new String[0]);
      for (String key : keys) result.add(getEncrypted(key, ImmutableSmartCard.class));
      return result;
   }

   @Override
   public void deleteSmartCard(String smartCardId) {
      act(db -> db.del(WALLET_SMART_CARD + smartCardId));
   }

   @Override
   public void saveSmartCardDetails(SmartCardDetails smartCardDetails) {
      putEncrypted(WALLET_DETAILS_SMART_CARD + smartCardDetails.smartCardId(), smartCardDetails);
   }

   @Override
   public SmartCardDetails getSmartCardDetails(String smartCardId) {
      return getEncrypted(WALLET_DETAILS_SMART_CARD + smartCardId, ImmutableSmartCardDetails.class);
   }

   @Override
   public void deleteSmartCardDetails(String smartCardId) {
      act(db -> db.del(WALLET_DETAILS_SMART_CARD + smartCardId));
   }

   @Override
   public void setActiveSmartCardId(String scid) {
      putEncrypted(WALLET_ACTIVE_SMART_CARD_ID, scid);
   }

   @Override
   public String getActiveSmartCardId() {
      return getEncrypted(WALLET_ACTIVE_SMART_CARD_ID, String.class);
   }

   @Override
   public void saveWalletCardsList(List<Card> items) {
      putEncrypted(WALLET_CARDS_LIST, items);
   }

   @Override
   public List<Card> readWalletCardsList() {
      return getEncryptedList(WALLET_CARDS_LIST);
   }

   public void deleteWalletCardList() {
      act(db -> {
         db.del(WALLET_CARDS_LIST);
         db.del(WALLET_DEFAULT_BANK_CARD);
      });
   }

   @Override
   public void saveWalletDefaultCardId(String defaultCardId) {
      if (defaultCardId == null) return;
      putEncrypted(WALLET_DEFAULT_BANK_CARD, defaultCardId);
   }

   @Override
   public String readWalletDefaultCardId() {
      return getEncrypted(WALLET_DEFAULT_BANK_CARD, String.class);
   }

   @Override
   public void saveDefaultAddress(AddressInfo addressInfo) {
      putEncrypted(WALLET_DEFAULT_ADDRESS, addressInfo);
   }

   @Override
   public AddressInfo readDefaultAddress() {
      return getEncrypted(WALLET_DEFAULT_ADDRESS, ImmutableAddressInfo.class);
   }

   @Override
   public void deleteDefaultAddress() {
      act(db -> db.del(WALLET_DEFAULT_ADDRESS));
   }

   @Override
   public void saveWalletTermsAndConditions(TermsAndConditions data) {
      putEncrypted(WALLET_TERMS_AND_CONDITIONS, data);
   }

   @Override
   public TermsAndConditions getWalletTermsAndConditions() {
      return getEncrypted(WALLET_TERMS_AND_CONDITIONS, ImmutableTermsAndConditions.class);
   }

   @Override
   public void deleteTermsAndConditions() {
      act(db -> db.del(WALLET_TERMS_AND_CONDITIONS));
   }

   @Override
   public void saveWalletDeviceStorage(SimpleDeviceStorage deviceStorage) {
      putEncrypted(WALLET_DEVICE_STORAGE, deviceStorage);
   }

   @Override
   public SimpleDeviceStorage getWalletDeviceStorage() {
      return getEncrypted(WALLET_DEVICE_STORAGE, SimpleDeviceStorage.class);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Settings
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveSettings(List<Setting> settingsList, boolean withClear) {
      act(db -> {
         if (withClear) clearSettings(db);
         //
         for (Setting settings : settingsList) {
            db.put(SETTINGS_KEY + settings.getType().name() + settings.getName(), settings);
         }
      });
   }

   @Override
   public List<Setting> getSettings() {
      return actWithResult(db -> {
         List<Setting> settingsList = new ArrayList<>();
         String[] keys = db.findKeys(SETTINGS_KEY);
         for (String key : keys) {
            if (key.contains(Setting.Type.FLAG.name())) {
               settingsList.add(db.get(key, FlagSetting.class));
            } else if (key.contains(Setting.Type.SELECT.name())) {
               settingsList.add(db.get(key, SelectSetting.class));
            }
         }
         return settingsList;
      }).or(Collections.emptyList());
   }

   @Override
   public void clearSettings(DB snappyDb) throws SnappydbException {
      String[] settingsKeys = snappyDb.findKeys(SETTINGS_KEY);
      for (String key : settingsKeys) {
         snappyDb.del(key);
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Suggest photos
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveLastSuggestedPhotosSyncTime(long time) {
      act(db -> db.putLong(SUGGESTED_PHOTOS_SYNC_TIME, time));
   }

   @Override
   public long getLastSuggestedPhotosSyncTime() {
      return actWithResult(db -> db.getLong(SUGGESTED_PHOTOS_SYNC_TIME)).or(0L);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Photo List Tasks
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void savePhotoEntityList(TripImagesType type, int userId, List<IFullScreenObject> items) {
      putList(IMAGE + ":" + type + ":" + userId, items);
   }

   @Override
   public List<IFullScreenObject> readPhotoEntityList(TripImagesType type, int userId) {
      return readList(IMAGE + ":" + type + ":" + userId, IFullScreenObject.class);
   }

   @Override
   public void saveLastSelectedVideoLocale(VideoLocale videoLocale) {
      act(db -> db.put(LAST_SELECTED_VIDEO_LOCALE, videoLocale));
   }

   @Override
   public VideoLocale getLastSelectedVideoLocale() {
      return actWithResult(db -> db.get(LAST_SELECTED_VIDEO_LOCALE, VideoLocale.class)).orNull();
   }

   @Override
   public void saveLastSelectedVideoLanguage(VideoLanguage videoLocale) {
      act(db -> db.put(LAST_SELECTED_VIDEO_LANGUAGE, videoLocale));
   }

   @Override
   public VideoLanguage getLastSelectedVideoLanguage() {
      return actWithResult(db -> db.get(LAST_SELECTED_VIDEO_LANGUAGE, VideoLanguage.class)).orNull();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Notifications counters
   ///////////////////////////////////////////////////////////////////////////

   /**
    * All notifications
    */
   @Override
   public void saveBadgeNotificationsCount(int notificationsCount) {
      act(db -> db.putInt(BADGE_NOTIFICATIONS_COUNT, notificationsCount));
   }

   /**
    * All notifications
    */
   @Override
   public int getBadgeNotificationsCount() {
      return actWithResult(db -> db.getInt(BADGE_NOTIFICATIONS_COUNT)).or(0);
   }

   @Override
   public void saveCountFromHeader(String headerKey, int count) {
      act(db -> db.putInt(headerKey, count));
   }

   @Override
   public int getExclusiveNotificationsCount() {
      return actWithResult(db -> db.getInt(EXCLUSIVE_NOTIFICATIONS_COUNT)).or(0);
   }

   @Override
   public int getFriendsRequestsCount() {
      return actWithResult(db -> db.getInt(FRIEND_REQUEST_COUNT)).or(0);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Cached translations
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveTranslation(String originalText, String translation, String toLanguage) {
      act(db -> db.put(TRANSLATION + originalText + toLanguage, translation));
   }

   @Override
   public String getTranslation(String originalText, String toLanguage) {
      return actWithResult(db -> db.get(TRANSLATION + originalText + toLanguage)).or("");
   }

   ///////////////////////////////////////////////////////////////////////////
   // Circles
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveCircles(List<Circle> circles) {
      if (circles == null) circles = new ArrayList<>();
      putList(CIRCLES, circles);
   }

   @Override
   public List<Circle> getCircles() {
      return readList(CIRCLES, Circle.class);
   }

   @Override
   public void saveFilterCircle(Circle circle) {
      act(db -> db.put(FILTER_CIRCLE, circle));
   }

   @Override
   public Circle getFilterCircle() {
      return actWithResult(db -> db.get(FILTER_CIRCLE, Circle.class)).orNull();
   }

   @Override
   public Circle getFeedFriendPickedCircle() {
      return actWithResult(db -> db.get(FILTER_FEED_FRIEND_FILTER_CIRCLE, Circle.class)).orNull();
   }

   @Override
   public void saveFeedFriendPickedCircle(Circle circle) {
      act(db -> db.put(FILTER_FEED_FRIEND_FILTER_CIRCLE, circle));
   }

   ///////////////////////////////////////////////////////////////////////////
   //
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public String getGcmRegToken() {
      return actWithResult(db -> db.get(GCM_REG_TOKEN)).orNull();
   }

   @Override
   public void setGcmRegToken(String token) {
      act(db -> db.put(GCM_REG_TOKEN, token));
   }

   @Override
   public void saveSocialViewPagerState(SocialViewPagerState state) {
      act(db -> db.put(SOCIAL_VIEW_PAGER_STATE, state));
   }

   @Override
   public SocialViewPagerState getSocialViewPagerState() {
      return actWithResult(db -> db.get(SOCIAL_VIEW_PAGER_STATE, SocialViewPagerState.class)).orNull();
   }

   @Override
   public List<FeedbackType> getFeedbackTypes() {
      return readList(FEEDBACK_TYPES, FeedbackType.class);
   }

   @Override
   public void setFeedbackTypes(List<FeedbackType> types) {
      clearAllForKey(FEEDBACK_TYPES);
      putList(FEEDBACK_TYPES, types);
   }


   ///////////////////////////////////////////////////////////////////////////
   // GCM
   ///////////////////////////////////////////////////////////////////////////

   private interface SnappyAction {
      void call(DB db) throws SnappydbException;
   }

   private interface SnappyResult<T> {
      T call(DB db) throws SnappydbException;
   }

   ///////////////////////////////////////////////////////////////////////////
   // DTL
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveDtlLocation(DtlLocation dtlLocation) {
      // list below is a hack to allow manipulating DtlLocation class since it is an interface
      List<DtlLocation> location = new ArrayList<>();
      location.add(dtlLocation);
      putList(DTL_SELECTED_LOCATION, location);
   }

   @Override
   public void cleanDtlLocation() {
      clearAllForKey(DTL_SELECTED_LOCATION);
   }

   @Override
   @Nullable
   public DtlLocation getDtlLocation() {
      // list below is a hack to allow manipulating DtlLocation class since it is an interface
      List<DtlLocation> location = readList(DTL_SELECTED_LOCATION, DtlLocation.class);
      if (location.isEmpty()) return DtlLocation.UNDEFINED;
      else return location.get(0);
   }

   @Override
   public void saveDtlMerhants(List<DtlMerchant> merchants) {
      clearAllForKey(DTL_MERCHANTS);
      putList(DTL_MERCHANTS, merchants);
   }

   @Override
   public List<DtlMerchant> getDtlMerchants() {
      return readList(DTL_MERCHANTS, DtlMerchant.class);
   }

   @Override
   public void saveAmenities(Collection<DtlMerchantAttribute> amenities) {
      clearAllForKey(DTL_AMENITIES);
      putList(DTL_AMENITIES, amenities);
   }

   @Override
   public List<DtlMerchantAttribute> getAmenities() {
      return readList(DTL_AMENITIES, DtlMerchantAttribute.class);
   }

   @Override
   public void clearMerchantData() {
      clearAllForKeys(DTL_MERCHANTS, DTL_AMENITIES, DTL_TRANSACTION_PREFIX);
   }

   @Override
   public void saveLastMapCameraPosition(Location location) {
      act(db -> db.put(DTL_LAST_MAP_POSITION, location));
   }

   @Override
   public Location getLastMapCameraPosition() {
      return actWithResult(db -> db.getObject(DTL_LAST_MAP_POSITION, Location.class)).orNull();
   }

   @Override
   public void cleanLastMapCameraPosition() {
      clearAllForKey(DTL_LAST_MAP_POSITION);
   }

   @Override
   public void saveLastSelectedOffersOnlyToogle(boolean state) {
      act(db -> db.putBoolean(DTL_SHOW_OFFERS_ONLY_TOGGLE, state));
   }

   @Override
   public Boolean getLastSelectedOffersOnlyToggle() {
      return actWithResult(db -> db.getBoolean(DTL_SHOW_OFFERS_ONLY_TOGGLE)).or(Boolean.FALSE);
   }

   @Override
   public void cleanLastSelectedOffersOnlyToggle() {
      clearAllForKey(DTL_SHOW_OFFERS_ONLY_TOGGLE);
   }

   ///////////////////////////////////////////////////////////////////////////
   // DTL Transaction
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public DtlTransaction getDtlTransaction(String id) {
      return actWithResult(db -> db.getObject(DTL_TRANSACTION_PREFIX + id, ImmutableDtlTransaction.class)).orNull();
   }

   @Override
   public void saveDtlTransaction(String id, DtlTransaction dtlTransaction) {
      act(db -> db.put(DTL_TRANSACTION_PREFIX + id, dtlTransaction));
   }

   @Override
   public void deleteDtlTransaction(String id) {
      act(db -> db.del(DTL_TRANSACTION_PREFIX + id));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Notifications
   ///////////////////////////////////////////////////////////////////////////

   private static final Map<String, Class<? extends FeedItem>> feedItemsMapping = new HashMap<>();

   static {
      feedItemsMapping.put(NOTIFICATIONS + UNDEFINED_FEED_ITEM, UndefinedFeedItem.class);
      feedItemsMapping.put(NOTIFICATIONS + TRIP_FEED_ITEM, TripFeedItem.class);
      feedItemsMapping.put(NOTIFICATIONS + PHOTO_FEED_ITEM, PhotoFeedItem.class);
      feedItemsMapping.put(NOTIFICATIONS + BUCKET_FEED_ITEM, BucketFeedItem.class);
      feedItemsMapping.put(NOTIFICATIONS + POST_FEED_ITEM, PostFeedItem.class);
   }

   @Override
   public void saveNotifications(List<FeedItem> notifications) {
      for (Map.Entry<String, Class<? extends FeedItem>> entry : feedItemsMapping.entrySet()) {
         saveNotificationByType(notifications, entry.getValue(), entry.getKey());
      }
   }

   private void saveNotificationByType(List<FeedItem> notifications, Class itemClass, String typeKey) {
      List<FeedItem> notificationsByClass = Queryable.from(notifications)
            .filter(item -> item.getClass().equals(itemClass))
            .toList();
      putList(typeKey, notificationsByClass);
   }

   @Override
   public List<FeedItem> getNotifications() {
      List<FeedItem> feedItems = new ArrayList<>();
      for (Map.Entry<String, Class<? extends FeedItem>> entry : feedItemsMapping.entrySet()) {
         feedItems.addAll(readList(entry.getKey(), entry.getValue()));
      }
      return Queryable.from(feedItems)
            .sort((feedItemL, feedItemR) -> feedItemR.getCreatedAt().compareTo(feedItemL.getCreatedAt()))
            .toList();
   }

   @Override
   public void savePodcasts(List<Podcast> podcasts) {
      if (podcasts == null) podcasts = new ArrayList<>();
      putList(PODCASTS, podcasts);
   }

   @Override
   public List<Podcast> getPodcasts() {
      return readList(PODCASTS, Podcast.class);
   }

   @Override
   public void saveTrips(List<TripModel> tripModels) {
      if (tripModels == null) tripModels = new ArrayList<>();
      putList(TRIPS, tripModels);
   }

   @Override
   public List<TripModel> getTrips() {
      return readList(TRIPS, TripModel.class);
   }

   @Override
   public void savePins(List<Pin> pins) {
      if (pins == null) pins = new ArrayList<>();
      putList(PINS, pins);
   }

   @Override
   public List<Pin> getPins() {
      return readList(PINS, Pin.class);
   }

   @Override
   public void saveTripDetails(TripModel tripModel) {
      act(db -> db.put(TRIPS_DETAILS + tripModel.getUid(), tripModel));
   }

   @Override
   public void saveTripsDetails(List<TripModel> trips) {
      act(db -> {
         for (TripModel tripModel : trips) {
            db.put(TRIPS_DETAILS + tripModel.getUid(), tripModel);
         }
      });
   }

   @Override
   public TripModel getTripDetail(String uid) {
      return actWithResult(db -> db.get(TRIPS_DETAILS + uid, TripModel.class)).orNull();
   }

   @Override
   public List<TripModel> getTripsDetailsForUids(List<String> uids) {
      return actWithResult(db -> {
         List<TripModel> tripModels = new ArrayList<>();
         for (String uid : uids) {
            TripModel tripModel = db.get(TRIPS_DETAILS + uid, TripModel.class);
            if (tripModel != null) tripModels.add(tripModel);
         }
         return tripModels;
      }).or(new ArrayList<>());
   }

}
