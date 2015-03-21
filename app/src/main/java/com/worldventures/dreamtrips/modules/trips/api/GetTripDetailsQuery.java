package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;

public class GetTripDetailsQuery extends Query<TripDetails> {

    private int tripId;

    public GetTripDetailsQuery(int tripId) {
        super(TripDetails.class);
        this.tripId = tripId;
    }

    @Override
    public TripDetails loadDataFromNetwork() throws Exception {
        return getService().getDetails(tripId);
    }
}
