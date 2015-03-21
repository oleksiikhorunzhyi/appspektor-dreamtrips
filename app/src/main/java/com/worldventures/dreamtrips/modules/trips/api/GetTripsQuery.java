package com.worldventures.dreamtrips.modules.trips.api;

import android.util.Log;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.Trip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class GetTripsQuery extends DreamTripsRequest<ArrayList<Trip>> {

    private SnappyRepository db;
    private boolean fromApi;
    private Prefs prefs;

    public GetTripsQuery(SnappyRepository snappyRepository, Prefs prefs, boolean fromApi) {
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
