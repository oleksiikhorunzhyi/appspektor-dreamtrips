package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import java.util.ArrayList;
import java.util.Calendar;

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
        if (fromApi) {
            this.fromApi = false;
            db.saveTrips(getService().getTrips());
            data.addAll(db.getTrips());
            prefs.put(Prefs.LAST_SYNC, Calendar.getInstance().getTimeInMillis());
        } else {
            data.addAll(db.getTrips());
        }
        return data;
    }


    @Override
    public int getErrorMessage() {
        return R.string.string_failed_to_load_trips;
    }
}
