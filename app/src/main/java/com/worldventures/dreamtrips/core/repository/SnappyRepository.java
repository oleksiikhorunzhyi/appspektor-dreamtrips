package com.worldventures.dreamtrips.core.repository;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by 1 on 27.02.15.
 */
public class SnappyRepository {

    public static final String REGIONS = "regions";
    public static final String ACTIVITIES = "activities";
    public static final String BUCKET_LIST = "buckets";

    public static final String TRIP_KEY = "trip";
    public static final String BUCKET_KEY = "bucket";
    public static final String IMAGE_UPLOAD_TASK_KEY = "image_upload_task_key";


    private Context context;
    private ExecutorService executorService;

    public SnappyRepository(Context context) {
        checkNotNull(context);
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public Boolean isEmpty(String key) throws ExecutionException, InterruptedException {
        Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                DB snappyDb = DBFactory.open(context);
                String[] keys = snappyDb.findKeys(key);
                snappyDb.close();
                return keys == null || keys.length == 0;
            }
        });
        return future.get();

    }


    public <T> void putList(List<T> list, String key, Class<T> clazz) {
        executorService.execute(() -> {
            try {
                DB db = DBFactory.open(context);
                db.put(key, list.toArray());
                db.close();
            } catch (SnappydbException e) {

            }
        });
    }

    public <T> List<T> readList(String key, Class<T> clazz) throws ExecutionException, InterruptedException {
        Future<List<T>> future = executorService.submit(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                DB db = DBFactory.open(context);
                List<T> result = new ArrayList<T>();
                if (db.exists(key)) {
                    T[] array = db.getObjectArray(key, clazz);
                    db.close();
                    return Arrays.asList(array);
                }
                db.close();
                return result;

            }
        });
        return future.get();
    }

    public void saveBucketList(List<BucketItem> items, String type) {
        putList(items, BUCKET_LIST + ":" + type, BucketItem.class);
    }

    public List<BucketItem> readBucketList(String type) throws ExecutionException, InterruptedException {
        List<BucketItem> list = readList(BUCKET_LIST + ":" + type, BucketItem.class);
        Collections.sort(list, (lhs, rhs) -> {
            if (lhs.isDone() == rhs.isDone()) {
                return 0;
            } else if (lhs.isDone() && !rhs.isDone()) {
                return 1;
            } else {
                return -1;
            }
        });
        return list;
    }

    public void saveTrips(List<Trip> list) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                for (Trip trip : list) {
                    snappyDb.put(TRIP_KEY + trip.getId(), trip);
                }
                snappyDb.close();
            } catch (SnappydbException e) {

            }
        });
    }

    public void saveTrip(Trip trip) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                snappyDb.put(TRIP_KEY + trip.getId(), trip);
                snappyDb.close();
            } catch (SnappydbException e) {

            }
        });
    }

    public List<Trip> getTrips() throws ExecutionException, InterruptedException {
        Future<List<Trip>> future = executorService.submit(new Callable<List<Trip>>() {
            @Override
            public List<Trip> call() throws Exception {
                DB db = DBFactory.open(context);
                List<Trip> trips = new ArrayList<>();

                try {
                    String[] keys = db.findKeys(TRIP_KEY);
                    for (String key : keys) {
                        trips.add(db.get(key, Trip.class));
                    }
                } catch (SnappydbException e) {
                    Log.e(SnappyRepository.class.getSimpleName(), "", e);
                }
                db.close();

                Collections.sort(trips, (lhs, rhs) -> {
                    if (lhs.getStartDateMillis() < rhs.getStartDateMillis()) {
                        return -1;
                    } else if (lhs.getStartDateMillis() == rhs.getStartDateMillis()) {
                        return 0;
                    } else {
                        return 1;
                    }
                });

                return trips;
            }
        });
        return future.get();

    }

    public void saveUploadImageTask(ImageUploadTask ut) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                snappyDb.put(IMAGE_UPLOAD_TASK_KEY + ut.getTaskId(), ut);
                snappyDb.close();
            } catch (SnappydbException e) {

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
                Log.e("snappy", "", e);
            }
        });
    }


    public List<ImageUploadTask> getAllImageUploadTask() {

        Future<List<ImageUploadTask>> future = executorService.submit(new Callable<List<ImageUploadTask>>() {
            @Override
            public List<ImageUploadTask> call() throws Exception {
                DB db = DBFactory.open(context);
                List<ImageUploadTask> tasks = new ArrayList<>();
                try {
                    String[] keys = db.findKeys(IMAGE_UPLOAD_TASK_KEY);
                    for (String key : keys) {
                        tasks.add(db.get(key, ImageUploadTask.class));
                    }
                } catch (SnappydbException e) {
                    Log.e("snappy", "", e);
                }
                db.close();

                return tasks;
            }
        });
        try {
            return future.get();
        } catch (Exception e) {
            Log.e("snappy", "", e);

        }
        return new ArrayList<>();
    }
}
