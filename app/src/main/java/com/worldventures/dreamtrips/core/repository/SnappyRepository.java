package com.worldventures.dreamtrips.core.repository;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.utils.ValidationUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;

public class SnappyRepository {

    public static final String REGIONS = "regions_new";
    public static final String CATEGORIES = "categories";
    public static final String ACTIVITIES = "activities_new";
    public static final String BUCKET_LIST = "bucketItems";
    private static final String RECENT_BUCKET_COUNT = "recent_bucket_items_count";

    public static final String TRIP_KEY = "trip_rezopia";
    public static final String IMAGE_UPLOAD_TASK_KEY = "image_upload_task_key";
    public static final String BUCKET_PHOTO_UPLOAD_TASK_KEY = "bucket_photo_upload_task_key";
    public static final String VIDEO_UPLOAD_ENTITY = "VIDEO_UPLOAD_ENTITY";
    public static final String INVITE_MEMBER = "INVITE_MEMBER ";

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

    private interface SnappyAction {
        void call(DB db) throws SnappydbException;
    }

    private interface SnappyResult<T> {
        T call(DB db) throws SnappydbException;
    }

    private void act(SnappyAction action) {
        executorService.execute(() -> {
            DB snappyDb = null;
            try {
                snappyDb = DBFactory.open(context);
                action.call(snappyDb);
            } catch (SnappydbException e) {
                Timber.w(e, "DB fails to act with result");
            } finally {
                if (snappyDb != null)
                    try {
                        snappyDb.close();
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
                Timber.w(e, "DB fails to act with result", e);
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

    ///////////////////////////////////////////////////////////////////////////
    // Public
    ///////////////////////////////////////////////////////////////////////////

    public void clearAll() {
        act((db) -> db.destroy());
    }

    public Boolean isEmpty(String key) {
        return actWithResult((db) -> {
            String[] keys = db.findKeys(key);
            return keys == null || keys.length == 0;
        }).or(false);
    }

    public <T> void putList(String key, List<T> list) {
        act(db -> db.put(key, list.toArray()));
    }

    public <T> List<T> readList(String key, Class<T> clazz) {
        return actWithResult(db -> Arrays.asList(db.getObjectArray(key, clazz)))
                .or(Collections.emptyList());
    }

    ///////////////////////////////////////////////////////////////////////////
    // BucketItems
    ///////////////////////////////////////////////////////////////////////////

    public void saveBucketList(List<BucketItem> items, String type) {
        putList(BUCKET_LIST + ":" + type, items);
    }

    public List<BucketItem> readBucketList(String type) {
        List<BucketItem> list = readList(BUCKET_LIST + ":" + type, BucketItem.class);
        Collections.sort(list, (lhs, rhs) -> {
            if (lhs.isDone() == rhs.isDone()) return 0;
            else if (lhs.isDone() && !rhs.isDone()) return 1;
            else return -1;
        });
        return list;
    }

    public void saveRecentlyAddedBucketItems(String type, final int count) {
        act(db -> db.putInt(RECENT_BUCKET_COUNT + ":" + type, count));
    }

    public int getRecentlyAddedBucketItems(String type) {
        return actWithResult(db -> db.getInt(RECENT_BUCKET_COUNT + ":" + type))
                .or(0);
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

    public void saveDownloadVideoEntity(CachedEntity e) {
        act(db -> db.put(VIDEO_UPLOAD_ENTITY + e.getUuid(), e));
    }

    public CachedEntity getDownloadVideoEntity(String id) {
        return actWithResult(db -> db.get(VIDEO_UPLOAD_ENTITY + id, CachedEntity.class))
                .orNull();
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
    // Image Tasks
    ///////////////////////////////////////////////////////////////////////////

    public void saveUploadImageTask(ImageUploadTask ut) {
        act(db -> db.put(IMAGE_UPLOAD_TASK_KEY + ut.getTaskId(), ut));
    }

    public void removeImageUploadTask(ImageUploadTask ut) {
        act(db -> db.del(IMAGE_UPLOAD_TASK_KEY + ut.getTaskId()));
    }

    public List<ImageUploadTask> getAllImageUploadTask() {
        return actWithResult(db -> {
            List<ImageUploadTask> tasks = new ArrayList<>();
            String[] keys = db.findKeys(IMAGE_UPLOAD_TASK_KEY);
            for (String key : keys) {
                tasks.add(db.get(key, ImageUploadTask.class));
            }
            return tasks;
        }).or(Collections.emptyList());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Photo Tasks
    ///////////////////////////////////////////////////////////////////////////

    public void saveBucketPhotoTask(BucketPhotoUploadTask task) {
        act(db -> db.put(BUCKET_PHOTO_UPLOAD_TASK_KEY + task.getTaskId(), task));
    }

    public void removeBucketPhotoTask(BucketPhotoUploadTask task) {
        act(db -> db.del(BUCKET_PHOTO_UPLOAD_TASK_KEY + task.getTaskId()));
    }


    public void addInviteMember(Member member) {
        act(db -> db.put(INVITE_MEMBER + member.getId(), member));
    }

    public List<Member> getInviteMembers(){
        return actWithResult(db -> {
            List<Member> members = new ArrayList<>();
            String[] keys = db.findKeys(INVITE_MEMBER);
            for (String key : keys) {
                members.add(db.get(key, Member.class));
            }
            return members;
        }).or(Collections.emptyList());
    }
}
