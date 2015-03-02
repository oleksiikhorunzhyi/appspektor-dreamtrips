package com.worldventures.dreamtrips.core.repository;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.core.model.Trip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.realm.Realm;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by 1 on 27.02.15.
 */
public class SnappyRepository {

    public static final String REGIONS = "regions";
    public static final String ACTIVITIES = "activities";

    public static final String TRIP_KEY = "trip";
    public static final String BUCKET_KEY = "bucket";


    private Context context;
    private ExecutorService executorService;

    public SnappyRepository(Context context) {
        checkNotNull(context);
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
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
                T[] result = db.getObjectArray(key, clazz);
                db.close();
                return Arrays.asList(result);
            }
        });
        return future.get();
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

    public void addBucketItem(BucketItem bucketItem, String type) {
        executorService.execute(() -> {
            try {
                DB snappyDb = DBFactory.open(context);
                String[] keys = snappyDb.findKeys(BUCKET_KEY + ":" + type);
                if (keys == null || keys.length == 0) {
                    addToBucket(snappyDb, bucketItem, 0, type);
                } else {
                    BucketItem lastBucketItem = snappyDb.get(keys[keys.length - 1], BucketItem.class);
                    addToBucket(snappyDb, bucketItem, lastBucketItem.getId() + 1, type);
                }
                snappyDb.close();
            } catch (SnappydbException e) {

            }
        });
    }

    private void addToBucket(DB snappyDB, BucketItem bucketItem, int id, String type) throws SnappydbException {
        bucketItem.setId(id);
        snappyDB.put(BUCKET_KEY + ":" + type + ":" + id, bucketItem);
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

    public List<BucketItem> getBucketItems(String type) throws ExecutionException, InterruptedException {
        Future<List<BucketItem>> future = executorService.submit(new Callable<List<BucketItem>>() {
            @Override
            public List<BucketItem> call() throws Exception {
                DB snappyDb = DBFactory.open(context);
                List<BucketItem> bucketItems = new ArrayList<>();

                try {
                    String[] keys = snappyDb.findKeys(BUCKET_KEY + ":" + type);
                    for (String key : keys) {
                        bucketItems.add(snappyDb.get(key, BucketItem.class));
                    }
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }
                snappyDb.close();
                return bucketItems;
            }
        });
        return future.get();

    }

    public void deleteBucketItem(BucketItem object, String type) {
        executorService.execute(() -> {
            DB snappyDb = null;
            try {
                snappyDb = DBFactory.open(context);
                snappyDb.del(BUCKET_KEY + ":" + type + ":" + object.getId());
                snappyDb.close();
            } catch (SnappydbException e) {
                e.printStackTrace();
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
                    e.printStackTrace();
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
}
