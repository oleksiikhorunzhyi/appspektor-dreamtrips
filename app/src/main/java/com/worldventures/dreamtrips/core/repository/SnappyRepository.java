package com.worldventures.dreamtrips.core.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.techery.spares.storage.complex_objects.Optional;
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;

public class SnappyRepository {

    public static final String CIRCLES = "circles";
    public static final String REGIONS = "regions_new";
    public static final String CATEGORIES = "categories";
    public static final String ACTIVITIES = "activities_new";
    public static final String BUCKET_LIST = "bucket_items";
    public static final String TRIP_KEY = "trip_rezopia_v2";
    public static final String POST = "post";
    public static final String UPLOAD_TASK_KEY = "amazon_upload_task";
    public static final String VIDEO_UPLOAD_ENTITY = "VIDEO_UPLOAD_ENTITY";
    public static final String INVITE_MEMBER = "INVITE_MEMBER ";
    public static final String LAST_SELECTED_VIDEO_LOCALE = "LAST_SELECTED_VIDEO_LOCALE";
    public static final String LAST_SELECTED_VIDEO_LANGUAGE = "LAST_SELECTED_VIDEO_LANGUAGE ";
    public static final String IMAGE = "IMAGE";
    public static final String RECENT_BUCKET_COUNT = "recent_bucket_items_count";
    public static final String OPEN_BUCKET_TAB_TYPE = "open_bucket_tab_type";
    public static final String BADGE_NOTIFICATIONS_COUNT = "badge_notifications_count";
    public static final String EXCLUSIVE_NOTIFICATIONS_COUNT = "Unread-Notifications-Count"; // WARNING must be equal to server header
    public static final String FRIEND_REQUEST_COUNT = "Friend-Requests-Count"; // WARNING must be equal to server header
    public static final String GCM_REG_TOKEN = "GCM_REG_TOKEN ";
    public static final String GCM_REG_ID_PERSISTED = "GCM_REG_ID_PERSISTED ";
    public static final String FILTER_CIRCLE = "FILTER_CIRCLE";
    public static final String FILTER_FEED_FRIEND_FILTER_CIRCLE = "FILTER_FEED_FRIEND_FILTER_CIRCLE";
    public static final String SOCIAL_VIEW_PAGER_STATE = "SOCIAL_VIEW_PAGER_STATE";

    public static final String DTL_MERCHANTS = "DTL_MERCHANTS";
    public static final String DTL_SELECTED_LOCATION = "DTL_SELECTED_LOCATION";
    public static final String DTL_TRANSACTION_PREFIX = "DTL_TRANSACTION_";
    public static final String DTL_DISTANCE_TOGGLE = "DTL_DISTANCE_TOGGLE";
    public static final String DTL_AMENITIES = "DTL_AMENITIES";

    private Context context;
    private ExecutorService executorService;

    public SnappyRepository(Context context) {
        ValidationUtils.checkNotNull(context);
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
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

    public void clearAll() {
        act((db) -> db.destroy());
    }

    public Boolean isEmpty(String key) {
        return actWithResult((db) -> {
            String[] keys = db.findKeys(key);
            return keys == null || keys.length == 0;
        }).or(false);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public
    ///////////////////////////////////////////////////////////////////////////

    public <T> void putList(String key, Collection<T> list) {
        act(db -> db.put(key, list.toArray()));
    }

    public <T> List<T> readList(String key, Class<T> clazz) {
        return actWithResult(db -> new ArrayList<>(Arrays.asList(db.getObjectArray(key, clazz))))
                .or(new ArrayList<>());
    }

    /**
     * Method is intended to delete all records for given key.
     *
     * @param key key to be deleted.
     */
    public void clearAllForKey(String key) {
        act(db -> {
            String[] placesKeys = db.findKeys(key);
            for (String placeKey : placesKeys) {
                db.del(placeKey);
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // BucketItems
    ///////////////////////////////////////////////////////////////////////////

    public void saveBucketList(List<BucketItem> items, String type) {
        saveBucketList(items, type, 0);
    }

    public void saveBucketList(List<BucketItem> items, String type, int userId) {
        String key = getBucketKey(type, userId);
        putList(key, items);
    }

    @NonNull
    private String getBucketKey(String type, int userId) {
        if (userId == 0) {
            throw new IllegalStateException("userId can't be 0");
        }
        String key = (BUCKET_LIST) + ":" + type;
        key += "_" + userId;
        return key.toLowerCase();
    }

    public List<BucketItem> readBucketList(String type, int userId) {
        List<BucketItem> list = readList(getBucketKey(type, userId), BucketItem.class);
        Collections.sort(list, (lhs, rhs) -> {
            if (lhs.isDone() == rhs.isDone()) return 0;
            else if (lhs.isDone() && !rhs.isDone()) return 1;
            else return -1;
        });
        return list;
    }

    public int getRecentlyAddedBucketItems(String type) {
        return actWithResult(db -> db.getInt(RECENT_BUCKET_COUNT + ":" + type))
                .or(0);
    }

    public void saveRecentlyAddedBucketItems(String type, final int count) {
        act(db -> db.putInt(RECENT_BUCKET_COUNT + ":" + type, count));
    }

    public void saveOpenBucketTabType(String type) {
        act(db -> db.put(OPEN_BUCKET_TAB_TYPE, type));
    }

    public String getOpenBucketTabType() {
        return actWithResult(db -> db.get(OPEN_BUCKET_TAB_TYPE)).orNull();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Trips
    ///////////////////////////////////////////////////////////////////////////

    public void saveTrip(TripModel trip) {
        act(db -> db.put(TRIP_KEY + trip.getTripId(), trip));
    }

    public void saveTrips(List<TripModel> list) {
        act(db -> {
            clearTrips(db);
            for (TripModel trip : list) {
                db.put(TRIP_KEY + trip.getTripId(), trip);
            }
        });
    }

    public List<TripModel> getTrips() {
        return actWithResult(db -> {
            List<TripModel> trips = new ArrayList<>();
            String[] keys = db.findKeys(TRIP_KEY);
            for (String key : keys) {
                trips.add(db.get(key, TripModel.class));
            }
            Collections.sort(trips, (lhs, rhs) -> {
                if (lhs.getStartDateMillis() < rhs.getStartDateMillis()) return -1;
                else if (lhs.getStartDateMillis() == rhs.getStartDateMillis()) return 0;
                else return 1;
            });
            return trips;
        }).or(Collections.emptyList());
    }

    public void clearTrips(DB snappyDb) throws SnappydbException {
        String[] tripKeys = snappyDb.findKeys(TRIP_KEY);
        for (String key : tripKeys) {
            snappyDb.del(key);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Video
    ///////////////////////////////////////////////////////////////////////////

    public void saveDownloadVideoEntity(CachedEntity e) {
        act(db -> db.put(VIDEO_UPLOAD_ENTITY + e.getUuid(), e));
    }

    public CachedEntity getDownloadVideoEntity(String id) {
        return actWithResult(db -> db.get(VIDEO_UPLOAD_ENTITY + id, CachedEntity.class))
                .orNull();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Image Tasks
    ///////////////////////////////////////////////////////////////////////////

    public void saveUploadTask(UploadTask uploadTask) {
        act(db -> db.put(UPLOAD_TASK_KEY + uploadTask.getFilePath(), uploadTask));
    }

    public UploadTask getUploadTask(String filePath) {
        return actWithResult(db -> db.get(UPLOAD_TASK_KEY + filePath, UploadTask.class)).orNull();
    }

    public void removeUploadTask(UploadTask uploadTask) {
        act(db -> db.del(UPLOAD_TASK_KEY + uploadTask.getFilePath()));
    }

    public void removeAllUploadTasks() {
        act(db -> Queryable.from(db.findKeys(UPLOAD_TASK_KEY)).forEachR(key -> {
            try {
                db.del(key);
            } catch (SnappydbException e) {
                Timber.e(e, "Error while deleting");
            }
        }));
    }

    public List<UploadTask> getUploadTasksForId(String linkedId) {
        List<UploadTask> items = getAllUploadTask();
        return Queryable.from(items)
                .filter(item -> linkedId.equals(item.getLinkedItemId()))
                .toList();
    }

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

    public void savePhotoEntityList(TripImagesType type, int userId, List<IFullScreenObject> items) {
        putList(IMAGE + ":" + type + ":" + userId, items);

    }

    public List<IFullScreenObject> readPhotoEntityList(TripImagesType type, int userId) {
        return readList(IMAGE + ":" + type + ":" + userId, IFullScreenObject.class);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Invites
    ///////////////////////////////////////////////////////////////////////////

    public void addInviteMember(Member member) {
        act(db -> db.put(INVITE_MEMBER + member.getId(), member));
    }

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

    public void saveLastSelectedVideoLocale(VideoLocale videoLocale) {
        act(db -> db.put(LAST_SELECTED_VIDEO_LOCALE, videoLocale));
    }

    public VideoLocale getLastSelectedVideoLocale() {
        return actWithResult(db -> db.get(LAST_SELECTED_VIDEO_LOCALE, VideoLocale.class))
                .orNull();
    }

    public void saveLastSelectedVideoLanguage(VideoLanguage videoLocale) {
        act(db -> db.put(LAST_SELECTED_VIDEO_LANGUAGE, videoLocale));
    }

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
    public void saveBadgeNotificationsCount(int notificationsCount) {
        act(db -> db.putInt(BADGE_NOTIFICATIONS_COUNT, notificationsCount));
    }

    /**
     * All notifications
     */
    public int getBadgeNotificationsCount() {
        return actWithResult(db -> db.getInt(BADGE_NOTIFICATIONS_COUNT)).or(0);
    }

    public void saveCountFromHeader(String headerKey, int count) {
        act(db -> db.putInt(headerKey, count));
    }

    public int getExclusiveNotificationsCount() {
        return actWithResult(db -> db.getInt(EXCLUSIVE_NOTIFICATIONS_COUNT)).or(0);
    }

    public int getFriendsRequestsCount() {
        return actWithResult(db -> db.getInt(FRIEND_REQUEST_COUNT)).or(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Circles
    ///////////////////////////////////////////////////////////////////////////

    public void saveCircles(List<Circle> circles) {
        putList(CIRCLES, circles);
    }

    public List<Circle> getCircles() {
        return readList(CIRCLES, Circle.class);
    }

    public void saveFilterCircle(Circle circle) {
        act(db -> db.put(FILTER_CIRCLE, circle));
    }

    public Circle getFilterCircle() {
        return actWithResult(db -> db.get(FILTER_CIRCLE, Circle.class)).orNull();
    }

    public Circle getFeedFriendPickedCircle() {
        return actWithResult(db -> db.get(FILTER_FEED_FRIEND_FILTER_CIRCLE, Circle.class)).orNull();
    }

    public void saveFeedFriendPickedCircle(Circle circle) {
        act(db -> db.put(FILTER_FEED_FRIEND_FILTER_CIRCLE, circle));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    public String getGcmRegToken() {
        return actWithResult(db -> db.get(GCM_REG_TOKEN)).orNull();
    }

    public void setGcmRegToken(String token) {
        act(db -> db.put(GCM_REG_TOKEN, token));
    }

    public void saveSocialViewPagerState(SocialViewPagerState state) {
        act(db -> db.put(SOCIAL_VIEW_PAGER_STATE, state));
    }

    public SocialViewPagerState getSocialViewPagerState() {
        return actWithResult(db -> db.get(SOCIAL_VIEW_PAGER_STATE, SocialViewPagerState.class)).orNull();
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

    public void saveDtlLocation(DtlLocation dtlLocation) {
        act(db -> db.put(DTL_SELECTED_LOCATION, dtlLocation));
    }

    public void cleanDtlLocation() {
        clearAllForKey(DTL_SELECTED_LOCATION);
    }

    public DtlLocation getDtlLocation() {
        return actWithResult(db -> db.getObject(DTL_SELECTED_LOCATION, DtlLocation.class))
                .orNull();
    }

    public void saveDtlMerhants(List<DtlMerchant> merchants) {
        clearAllForKey(DTL_MERCHANTS);
        putList(DTL_MERCHANTS, merchants);
    }

    public List<DtlMerchant> getDtlMerchants() {
        return readList(DTL_MERCHANTS, DtlMerchant.class);
    }

    public void saveAmenities(Collection<DtlMerchantAttribute> amenities) {
        clearAllForKey(DTL_AMENITIES);
        putList(DTL_AMENITIES, amenities);
    }

    public List<DtlMerchantAttribute> getAmenities() {
        return readList(DTL_AMENITIES, DtlMerchantAttribute.class);
    }

    public void clearMerchantData() {
        clearAllForKey(DTL_MERCHANTS);
        clearAllForKey(DTL_AMENITIES);
        clearAllForKey(DTL_TRANSACTION_PREFIX);
    }

    //TODO add implementation
    public DistanceType getMerchantsDistanceType() {
        return DistanceType.KMS;
    }

    ///////////////////////////////////////////////////////////////////////////
    // DTL Transaction
    ///////////////////////////////////////////////////////////////////////////

    public DtlTransaction getDtlTransaction(String id) {
        return actWithResult(db -> db.getObject(DTL_TRANSACTION_PREFIX + id, DtlTransaction.class)).orNull();
    }

    public void cleanDtlTransaction(String id, DtlTransaction dtlTransaction) {
        dtlTransaction.setUploadTask(null);
        dtlTransaction.setBillTotal(0.0d);
        dtlTransaction.setReceiptPhotoUrl(null);
        dtlTransaction.setCode(null);
        dtlTransaction.setVerified(false);
        dtlTransaction.setDtlTransactionResult(null);
        saveDtlTransaction(id, dtlTransaction);
    }

    public void saveDtlTransaction(String id, DtlTransaction dtlTransaction) {
        act(db -> db.put(DTL_TRANSACTION_PREFIX + id, dtlTransaction));
    }

    public void deleteDtlTransaction(String id) {
        act(db -> db.del(DTL_TRANSACTION_PREFIX + id));
    }

}
