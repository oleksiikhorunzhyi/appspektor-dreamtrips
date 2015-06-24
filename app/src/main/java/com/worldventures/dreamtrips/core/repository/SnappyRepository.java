package com.worldventures.dreamtrips.core.repository;

import android.content.Context;

import com.innahema.collections.query.queriables.Queryable;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.techery.spares.storage.complex_objects.Optional;
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.sephiroth.android.library.easing.Circ;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class SnappyRepository {

    public static final String CIRCLES = "circles";
    public static final String REGIONS = "regions_new";
    public static final String CATEGORIES = "categories";
    public static final String ACTIVITIES = "activities_new";
    public static final String BUCKET_LIST = "bucketItems";
    public static final String TRIP_KEY = "trip_rezopia_v2";
    public static final String IMAGE_UPLOAD_TASK_KEY = "image_upload_task_key";
    public static final String BUCKET_PHOTO_UPLOAD_TASK_KEY = "bucket_photo_upload_task_key";
    public static final String VIDEO_UPLOAD_ENTITY = "VIDEO_UPLOAD_ENTITY";
    public static final String INVITE_MEMBER = "INVITE_MEMBER ";
    public static final String IMAGE = "IMAGE";
    private static final String RECENT_BUCKET_COUNT = "recent_bucket_items_count";
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

    public <T> void putList(String key, List<T> list) {
        act(db -> db.put(key, list.toArray()));
    }

    public <T> List<T> readList(String key, Class<T> clazz) {
        return actWithResult(db -> new ArrayList<>(Arrays.asList(db.getObjectArray(key, clazz))))
                .or(new ArrayList<>());
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

    public int getRecentlyAddedBucketItems(String type) {
        return actWithResult(db -> db.getInt(RECENT_BUCKET_COUNT + ":" + type))
                .or(0);
    }

    public void saveRecentlyAddedBucketItems(String type, final int count) {
        act(db -> db.putInt(RECENT_BUCKET_COUNT + ":" + type, count));
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

    public void savePhotoEntityList(Type type, List<IFullScreenObject> items) {
        putList(IMAGE + ":" + type, items);

    }

    public List<IFullScreenObject> readPhotoEntityList(Type type) {
        return readList(IMAGE + ":" + type, IFullScreenObject.class);
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

    ///////////////////////////////////////////////////////////////////////////
    // Circles
    ///////////////////////////////////////////////////////////////////////////

    public void saveCircles(List<Circle> circles) {
        putList(CIRCLES, circles);
    }

    public List<Circle> getCircles() {
        return readList(CIRCLES, Circle.class);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    private interface SnappyAction {
        void call(DB db) throws SnappydbException;
    }


    private interface SnappyResult<T> {
        T call(DB db) throws SnappydbException;
    }
}
