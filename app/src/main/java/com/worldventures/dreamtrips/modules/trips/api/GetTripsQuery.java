package com.worldventures.dreamtrips.modules.trips.api;

import android.util.Log;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class GetTripsQuery extends DreamTripsRequest<ArrayList<TripModel>> {

    private SnappyRepository db;
    private boolean fromApi;
    private Prefs prefs;

    public GetTripsQuery(SnappyRepository snappyRepository, Prefs prefs, boolean fromApi) {
        super((Class<ArrayList<TripModel>>) new ArrayList<TripModel>().getClass());
        this.fromApi = fromApi;
        this.prefs = prefs;
        this.db = snappyRepository;
    }

    @Override
    public ArrayList<TripModel> loadDataFromNetwork() throws Exception {
        ArrayList<TripModel> data = new ArrayList<>();
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

    private boolean needUpdate() {
        long current = Calendar.getInstance().getTimeInMillis();
        return current - prefs.getLong(Prefs.LAST_SYNC) > DELTA || db.isEmpty(SnappyRepository.TRIP_KEY);
    }

}
