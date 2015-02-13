package com.worldventures.dreamtrips.core.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.reflect.TypeToken;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.utils.FileUtils;
import com.worldventures.dreamtrips.utils.busevents.TripLikedEvent;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by 1 on 13.02.15.
 */
public class TripsIntentService extends IntentService {

    public static final String TRIP_EXTRA = "TRIP";

    public TripsIntentService() {
        super("Trips saver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Trip trip = (Trip) intent.getSerializableExtra(TRIP_EXTRA);
        List<Trip> trips = FileUtils.parseJsonFromCache(this, new TypeToken<List<Trip>>() {
        }.getType(), FileUtils.TRIPS);

        for (Trip temp : trips) {
            if (temp.getId() == trip.getId()) {
                temp.setLiked(trip.isLiked());
                break;
            }
        }

        FileUtils.saveJsonToCache(this, trips, FileUtils.TRIPS);
    }
}
