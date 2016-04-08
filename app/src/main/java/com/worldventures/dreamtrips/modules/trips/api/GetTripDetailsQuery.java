package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;

public class GetTripDetailsQuery extends Query<TripDetails> {

    private String tripId;

    public GetTripDetailsQuery(String tripId) {
        super(TripDetails.class);
        this.tripId = tripId;
    }

    @Override
    public TripDetails loadDataFromNetwork() throws Exception {
        return getService().getDetails(tripId);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_item_details;
    }
}
