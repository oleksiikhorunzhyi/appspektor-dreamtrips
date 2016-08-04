package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/trips/{id}", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.GET)
public class GetTripDetailsHttpAction extends AuthorizedHttpAction {

    @Path("id") String tripId;

    @Response TripDetails tripDetails;

    public GetTripDetailsHttpAction(String tripId) {
        this.tripId = tripId;
    }

    public TripDetails getTripDetails() {
        return tripDetails;
    }
}
