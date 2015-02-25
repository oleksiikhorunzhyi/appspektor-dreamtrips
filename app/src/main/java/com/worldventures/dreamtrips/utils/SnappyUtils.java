package com.worldventures.dreamtrips.utils;

import android.content.Context;

import com.google.common.collect.Collections2;
import com.google.common.util.concurrent.Futures;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.model.Trip;

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

    public static final String TRIPS = "trips.json";
    public static final String REGIONS = "regions.json";
    public static final String ACTIVITIES = "activities.json";

    private static final String TRIP_KEY = "trip";

    public static void saveRegions(Context context, List<Region> list) {
        try {
            DB snappyDb = DBFactory.open(context);
            snappyDb.put(REGIONS, list.toArray());
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static List<Region> getRegions(Context context) {
        List<Region> list = new ArrayList<>();
        try {
            DB snappyDb = DBFactory.open(context);
            list.addAll(Arrays.asList(snappyDb.getObjectArray(REGIONS, Region.class)));
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveActivities(Context context, List<Activity> list) {
        try {
            DB snappyDb = DBFactory.open(context);
            snappyDb.put(ACTIVITIES, list.toArray());
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static List<Activity> getActivities(Context context) {
        List<Activity> list = new ArrayList<>();
        try {
            DB snappyDb = DBFactory.open(context);
            list.addAll(Arrays.asList(snappyDb.getObjectArray(ACTIVITIES, Activity.class)));
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveTrips(Context context, List<Trip> list) {
        try {
            DB snappyDb = DBFactory.open(context, FileUtils.TRIPS);
            for (Trip trip : list) {
                snappyDb.put(TRIP_KEY + trip.getId(), trip);
            }
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static void saveTrip(Context context, Trip trip) {
        try {
            DB snappyDb = DBFactory.open(context, FileUtils.TRIPS);
            snappyDb.put(TRIP_KEY + trip.getId(), trip);
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

    }

    public static List<Trip> getTrips(Context context) {
        List<Trip> trips = new ArrayList<>();

        try {
            DB snappyDb = DBFactory.open(context, FileUtils.TRIPS);
            String[] keys = snappyDb.findKeys(TRIP_KEY);
            for (String key : keys) {
                trips.add(snappyDb.get(key, Trip.class));
            }
            snappyDb.close();
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
    }
}
