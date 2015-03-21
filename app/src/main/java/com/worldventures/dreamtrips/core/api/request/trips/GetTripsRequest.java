package com.worldventures.dreamtrips.core.api.request.trips;

import android.util.Log;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
* Created by zen on 3/21/15.
*/
public class GetTripsRequest extends DreamTripsRequest<ArrayList<Trip>> {

    private SnappyRepository db;
    private boolean fromApi;
    private Prefs prefs;

    public GetTripsRequest(SnappyRepository snappyRepository, Prefs prefs, boolean fromApi) {
        super((Class<ArrayList<Trip>>) new ArrayList<Trip>().getClass());
        this.fromApi = fromApi;
        this.prefs = prefs;
        this.db = snappyRepository;
    }

    @Override
    public ArrayList<Trip> loadDataFromNetwork() throws Exception {
        ArrayList<Trip> data = new ArrayList<>();
        if (needUpdate() || fromApi) {
            this.fromApi = false;
            data.addAll(getService().getTrips());
            try {
                db.saveTrips(data);
            } catch (Exception e) {
                Log.e("", "", e);
            }
            prefs.put(Prefs.LAST_SYNC, Calendar.getInstance().getTimeInMillis());
        } else {
            data.addAll(db.getTrips());
        }
        return data;
    }

    private boolean needUpdate() throws ExecutionException, InterruptedException {
        long current = Calendar.getInstance().getTimeInMillis();
        return current - prefs.getLong(Prefs.LAST_SYNC) > DELTA || db.isEmpty(SnappyRepository.TRIP_KEY);
    }

}
