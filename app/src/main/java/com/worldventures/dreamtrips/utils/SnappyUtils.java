package com.worldventures.dreamtrips.utils;

import android.content.Context;

import com.google.common.collect.Collections2;
import com.google.common.util.concurrent.Futures;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

/**
 * Created by 1 on 25.02.15.
 */
public class SnappyUtils {

    /*public static final String REGIONS = "regions";
    public static final String ACTIVITIES = "activities";

    private static final String TRIP_KEY = "trip";
    private static final String BUCKET_KEY = "bucket";

    public static void saveRegions(DB snappyDb, Context context, List<Region> list) {
        try {
            snappyDb.put(REGIONS, list.toArray());
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static List<Region> getRegions(DB snappyDb, Context context) {
        List<Region> list = new ArrayList<>();
        try {
            list.addAll(Arrays.asList(snappyDb.getObjectArray(REGIONS, Region.class)));
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveActivities(DB snappyDb, Context context, List<Activity> list) {
        try {
            snappyDb.put(ACTIVITIES, list.toArray());
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static List<Activity> getActivities(DB snappyDb, Context context) {
        List<Activity> list = new ArrayList<>();
        try {
            list.addAll(Arrays.asList(snappyDb.getObjectArray(ACTIVITIES, Activity.class)));
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveTrips(DB snappyDb, Context context, List<Trip> list) {
        try {
            for (Trip trip : list) {
                snappyDb.put(TRIP_KEY + trip.getId(), trip);
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static void saveTrip(DB snappyDb, Context context, Trip trip) {
        try {
            snappyDb.put(TRIP_KEY + trip.getId(), trip);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

    }

    public static List<BucketItem> getBucketItems(DB snappyDb, Context context, BucketTabsFragment.Type type) {
        List<BucketItem> bucketItems = new ArrayList<>();

        try {
            String[] keys = snappyDb.findKeys(BUCKET_KEY + ":" + type.name());
            for (String key : keys) {
                bucketItems.add(snappyDb.get(key, BucketItem.class));
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        return bucketItems;
    }

    public static void addBucketItem(DB snappyDb, Context context, BucketItem bucketItem, String type) {
        try {
            String[] keys = snappyDb.findKeys(BUCKET_KEY + ":" + type);
            if (keys == null || keys.length == 0) {
                addToBucket(snappyDb, bucketItem, 0, type);
            } else {
                BucketItem lastBucketItem = snappyDb.get(keys[keys.length - 1], BucketItem.class);
                addToBucket(snappyDb, bucketItem, lastBucketItem.getId() + 1, type);
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    private static void addToBucket(DB snappyDB, BucketItem bucketItem, int id, String type) throws SnappydbException {
        snappyDB.put(BUCKET_KEY + ":" + type + ":" + id, bucketItem);
    }

    public static List<Trip> getTrips(DB snappyDb, Context context) {
        List<Trip> trips = new ArrayList<>();

        try {
            String[] keys = snappyDb.findKeys(TRIP_KEY);
            for (String key : keys) {
                trips.add(snappyDb.get(key, Trip.class));
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

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
    }*/
}
