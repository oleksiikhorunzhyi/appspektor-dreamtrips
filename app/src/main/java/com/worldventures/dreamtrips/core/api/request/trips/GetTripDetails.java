package com.worldventures.dreamtrips.core.api.request.trips;

import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.TripDetails;

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
