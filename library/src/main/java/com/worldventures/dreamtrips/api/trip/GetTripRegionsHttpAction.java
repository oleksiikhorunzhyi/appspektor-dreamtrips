package com.worldventures.dreamtrips.api.trip;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.trip.model.TripRegion;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/regions")
public class GetTripRegionsHttpAction extends AuthorizedHttpAction {

    @Response
    List<TripRegion> regions;

    public List<TripRegion> response() {
        return regions;
    }
}
