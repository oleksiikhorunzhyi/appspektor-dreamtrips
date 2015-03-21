package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;

public class GetTripDetails extends DreamTripsRequest<TripDetails> {

    private int tripId;

    public GetTripDetails(int tripId) {
        super(TripDetails.class);
        this.tripId = tripId;
    }

    @Override
    public TripDetails loadDataFromNetwork() throws Exception {
        return getService().getDetails(tripId);
    }
}
