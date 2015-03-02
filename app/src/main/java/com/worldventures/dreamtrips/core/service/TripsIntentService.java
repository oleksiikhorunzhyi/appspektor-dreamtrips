package com.worldventures.dreamtrips.core.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.reflect.TypeToken;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.utils.FileUtils;
import com.worldventures.dreamtrips.utils.SnappyUtils;
import com.worldventures.dreamtrips.utils.busevents.TripLikedEvent;

import java.util.Arrays;
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
    }
}
