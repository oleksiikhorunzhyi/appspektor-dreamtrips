package com.worldventures.dreamtrips.api.trip;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.trip.model.TripActivity;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/activities")
public class GetTripActivitiesHttpAction extends AuthorizedHttpAction {

    @Response
    List<TripActivity> activities;

    public List<TripActivity> response() {
        return activities;
    }
}
