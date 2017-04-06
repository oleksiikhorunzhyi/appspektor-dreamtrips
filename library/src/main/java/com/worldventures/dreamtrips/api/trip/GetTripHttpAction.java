package com.worldventures.dreamtrips.api.trip;

import com.worldventures.dreamtrips.api.entity.GetEntityHttpAction;
import com.worldventures.dreamtrips.api.trip.model.TripWithDetails;

import io.techery.janet.http.annotations.HttpAction;

@HttpAction("/api/{uid}")
public class GetTripHttpAction extends GetEntityHttpAction<TripWithDetails> {

    public GetTripHttpAction(String uid) {
        super(uid);
    }
}
