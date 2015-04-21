package com.worldventures.dreamtrips.core.repository;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.utils.ValidationUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
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

public class SnappyRepository {

    public static final String REGIONS = "regions_new";
    public static final String CATEGORIES = "categories";
    public static final String ACTIVITIES = "activities_new";
    public static final String BUCKET_LIST = "bucketItems";

    public static final String TRIP_KEY = "trip_rezopia";
    public static final String IMAGE_UPLOAD_TASK_KEY = "image_upload_task_key";
    public static final String BUCKET_PHOTO_UPLOAD_TASK_KEY = "bucket_photo_upload_task_key";
    public static final String VIDEO_UPLOAD_ENTITY = "VIDEO_UPLOAD_ENTITY";

    private Context context;
    private ExecutorService executorService;

    public SnappyRepository(Context context) {
        ValidationUtils.checkNotNull(context);
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void clearAll() {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                snappyDb.destroy();
                snappyDb.close();
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
        });
    }


    public Boolean isEmpty(String key) {
        Boolean empty = null;
        Future<Boolean> future = executorService.submit(() -> {
            DB snappyDb = DBFactory.open(context);
            String[] keys = snappyDb.findKeys(key);
            snappyDb.close();
            return keys == null || keys.length == 0;
        });

        try {
            empty = future.get();
        } catch (ExecutionException e) {
            Log.e(SnappyRepository.class.getSimpleName(), "", e);
        } catch (InterruptedException e) {
            Log.e(SnappyRepository.class.getSimpleName(), "", e);
        }

        return empty;
    }


    public <T> void putList(List<T> list, String key) {
        executorService.execute(() -> {
            try {
                DB db = DBFactory.open(context);
                db.put(key, list.toArray());
                db.close();
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
        });
    }

    public <T> List<T> readList(String key, Class<T> clazz) {
        List<T> list = null;

        Future<List<T>> future = executorService.submit(() -> {
            DB db = DBFactory.open(context);
            if (db.exists(key)) {
                T[] array = db.getObjectArray(key, clazz);
                db.close();
                return Arrays.asList(array);
            }
            db.close();
            return Collections.emptyList();
        });

        try {
            list = future.get();
        } catch (ExecutionException e) {
            Log.e(SnappyRepository.class.getSimpleName(), "", e);
        } catch (InterruptedException e) {
            Log.e(SnappyRepository.class.getSimpleName(), "", e);
        }

        return list;
    }

    public void saveBucketList(List<BucketItem> items, String type) {
        putList(items, BUCKET_LIST + ":" + type);
    }

    public List<BucketItem> readBucketList(String type) {
        List<BucketItem> list = null;
        list = readList(BUCKET_LIST + ":" + type, BucketItem.class);
        sortBucketList(list);
        return list;
    }

    private void sortBucketList(List<BucketItem> list) {
        Collections.sort(list, (lhs, rhs) -> {
            if (lhs.isDone() == rhs.isDone()) {
                return 0;
            } else if (lhs.isDone() && !rhs.isDone()) {
                return 1;
            } else {
                return -1;
            }
        });
    }

    public void clearTrips(DB snappyDb) throws SnappydbException {
        String[] tripKeys = snappyDb.findKeys(TRIP_KEY);
        for (String key : tripKeys) {
            snappyDb.del(key);
        }
    }

    public void saveTrips(List<TripModel> list) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                clearTrips(snappyDb);
                for (TripModel trip : list) {
                    snappyDb.put(TRIP_KEY + trip.getTripId(), trip);
                }
                snappyDb.close();
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
        });
    }

    public void saveTrip(TripModel trip) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                snappyDb.put(TRIP_KEY + trip.getTripId(), trip);
                snappyDb.close();
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
        });
    }

    public void saveDownloadVideoEntity(CachedEntity e) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                snappyDb.put(VIDEO_UPLOAD_ENTITY + e.getUuid(), e);
                snappyDb.close();
            } catch (SnappydbException ex) {
                Log.e(SnappyRepository.class.getSimpleName(), "", ex);
            }
        });
    }

    public CachedEntity getDownloadVideoEntity(String id) {
        Future<CachedEntity> future = executorService.submit(() -> {
            DB db = DBFactory.open(context);

            try {
                String[] keys = db.findKeys(VIDEO_UPLOAD_ENTITY + id);
                for (String key : keys) {
                    Log.v(SnappyRepository.class.getSimpleName(), key);
                    return db.get(key, CachedEntity.class);
                }
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
            db.close();
            return null;
        });

        CachedEntity entity = null;
        try {
            entity = future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(SnappyRepository.class.getSimpleName(), "", e);
        }

        return entity;
    }

    public List<TripModel> getTrips() {
        List<TripModel> list = null;


        Future<List<TripModel>> future = executorService.submit(() -> {
            DB db = DBFactory.open(context);
            List<TripModel> trips = new ArrayList<>();

            try {
                String[] keys = db.findKeys(TRIP_KEY);
                for (String key : keys) {
                    trips.add(db.get(key, TripModel.class));
                }
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
            db.close();

            sortTrips(trips);
            return trips;
        });

        try {
            list = future.get();
        } catch (ExecutionException e) {
            Log.e(SnappyRepository.class.getSimpleName(), "", e);
        } catch (InterruptedException e) {
            Log.e(SnappyRepository.class.getSimpleName(), "", e);
        }


        return list;
    }

    private void sortTrips(List<TripModel> trips) {
        Collections.sort(trips, (lhs, rhs) -> {
            if (lhs.getStartDateMillis() < rhs.getStartDateMillis()) {
                return -1;
            } else if (lhs.getStartDateMillis() == rhs.getStartDateMillis()) {
                return 0;
            } else {
                return 1;
            }
        });
    }


    public void saveUploadImageTask(ImageUploadTask ut) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                snappyDb.put(IMAGE_UPLOAD_TASK_KEY + ut.getTaskId(), ut);
                snappyDb.close();
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
        });
    }

    public void removeImageUploadTask(ImageUploadTask ut) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                snappyDb.del(IMAGE_UPLOAD_TASK_KEY + ut.getTaskId());
                snappyDb.close();
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
        });
    }


    public List<ImageUploadTask> getAllImageUploadTask() {

        Future<List<ImageUploadTask>> future = executorService.submit(() -> {
            DB db = DBFactory.open(context);
            List<ImageUploadTask> tasks = new ArrayList<>();
            try {
                String[] keys = db.findKeys(IMAGE_UPLOAD_TASK_KEY);
                for (String key : keys) {
                    tasks.add(db.get(key, ImageUploadTask.class));
                }
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
            db.close();

            return tasks;
        });

        try {
            return future.get();
        } catch (Exception e) {
            Log.e(SnappyRepository.class.getSimpleName(), "", e);
        }

        return new ArrayList<>();
    }

    public void saveBucketPhotoTask(BucketPhotoUploadTask task) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                snappyDb.put(BUCKET_PHOTO_UPLOAD_TASK_KEY + task.getTaskId(), task);
                snappyDb.close();
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
        });
    }


    public void removeBucketPhotoTask(BucketPhotoUploadTask task) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                snappyDb.del(BUCKET_PHOTO_UPLOAD_TASK_KEY + task.getTaskId());
                snappyDb.close();
            } catch (SnappydbException e) {
                Log.e(SnappyRepository.class.getSimpleName(), "", e);
            }
        });
    }
}
