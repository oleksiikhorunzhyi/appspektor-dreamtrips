package com.worldventures.dreamtrips.core.repository;

import android.content.Context;
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
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.settings.model.FlagSetting;
import com.worldventures.dreamtrips.modules.settings.model.SelectSetting;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;

public class SnappyRepositoryImpl implements SnappyRepository {
    private Context context;
    private ExecutorService executorService;
    private Kryo kryo;

    public SnappyRepositoryImpl(Context context) {
        ValidationUtils.checkNotNull(context);
        this.context = context;
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
                if (snappyDb != null)
                    try {
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
        return actWithResult(
                db -> {
                    Collection<T> result;
                    Input input = new Input();
                    try {
                        input.setBuffer(db.getBytes(key));
                        result = (Collection<T>) kryo.readClassAndObject(input);
                    } finally {
                        input.close();
                    }
                    return new ArrayList(result);
                }
        ).or(new ArrayList<>());
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
    public int getRecentlyAddedBucketItems(String type) {
        return actWithResult(db -> db.getInt(RECENT_BUCKET_COUNT + ":" + type))
                .or(0);
    }

    @Override
    public void saveRecentlyAddedBucketItems(String type, final int count) {
        act(db -> db.putInt(RECENT_BUCKET_COUNT + ":" + type, count));
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
    public void saveDownloadMediaEntity(CachedEntity e) {
        act(db -> db.put(MEDIA_UPLOAD_ENTITY + e.getUuid(), e));
    }

    @Override
    public CachedEntity getDownloadMediaEntity(String id) {
        return actWithResult(db -> db.get(MEDIA_UPLOAD_ENTITY + id, CachedEntity.class))
                .orNull();
    }

    @Override
    public void saveWalletCardsList(List<Card> items) {
        putList(WALLET_CARDS_LIST, items);
    }

    @Override
    public List<Card> readWalletCardsList() {
        return readList(WALLET_CARDS_LIST, Card.class);
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
    // Settings
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
    // Image Tasks
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Use it from PhotoUploadManager
     */
    @Override
    public void saveUploadTask(UploadTask uploadTask) {
        act(db -> db.put(UPLOAD_TASK_KEY + uploadTask.getFilePath(), uploadTask));
    }

    @Override
    public UploadTask getUploadTask(String filePath) {
        return actWithResult(db -> db.get(UPLOAD_TASK_KEY + filePath, UploadTask.class)).orNull();
    }

    @Override
    public void removeUploadTask(UploadTask uploadTask) {
        act(db -> db.del(UPLOAD_TASK_KEY + uploadTask.getFilePath()));
    }

    @Override
    public void removeAllUploadTasks() {
        act(db -> Queryable.from(db.findKeys(UPLOAD_TASK_KEY)).forEachR(key -> {
            try {
                db.del(key);
            } catch (SnappydbException e) {
                Timber.e(e, "Error while deleting");
            }
        }));
    }

    @Override
    public List<UploadTask> getAllUploadTask() {
        return actWithResult(db -> {
            List<UploadTask> tasks = new ArrayList<>();
            String[] keys = db.findKeys(UPLOAD_TASK_KEY);
            for (String key : keys) {
                tasks.add(db.get(key, UploadTask.class));
            }
            return tasks;
        }).or(Collections.emptyList());
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

    ///////////////////////////////////////////////////////////////////////////
    // Invites
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void addInviteMember(Member member) {
        act(db -> db.put(INVITE_MEMBER + member.getId(), member));
    }

    @Override
    public List<Member> getInviteMembers() {
        return actWithResult(db -> {
            List<Member> members = new ArrayList<>();
            String[] keys = db.findKeys(INVITE_MEMBER);
            for (String key : keys) {
                members.add(db.get(key, Member.class));
            }
            return members;
        }).or(Collections.emptyList());
    }

    @Override
    public void saveLastSelectedVideoLocale(VideoLocale videoLocale) {
        act(db -> db.put(LAST_SELECTED_VIDEO_LOCALE, videoLocale));
    }

    @Override
    public VideoLocale getLastSelectedVideoLocale() {
        return actWithResult(db -> db.get(LAST_SELECTED_VIDEO_LOCALE, VideoLocale.class))
                .orNull();
    }

    @Override
    public void saveLastSelectedVideoLanguage(VideoLanguage videoLocale) {
        act(db -> db.put(LAST_SELECTED_VIDEO_LANGUAGE, videoLocale));
    }

    @Override
    public VideoLanguage getLastSelectedVideoLanguage() {
        return actWithResult(db -> db.get(LAST_SELECTED_VIDEO_LANGUAGE, VideoLanguage.class))
                .orNull();
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
    public void setFeedbackTypes(ArrayList<FeedbackType> types) {
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
}
