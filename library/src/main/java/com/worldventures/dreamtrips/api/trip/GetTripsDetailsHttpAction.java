package com.worldventures.dreamtrips.api.trip;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.trip.model.TripWithoutDetails;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction("/api/trips/details")
public class GetTripsDetailsHttpAction extends AuthorizedHttpAction {

    @Query("trip_uids")
    public final String tripUids;

    @Response
    List<TripWithoutDetails> trips;

    public GetTripsDetailsHttpAction(List<String> tripUids) {
        super();
        StringBuilder sb = new StringBuilder();
        boolean skip = true;
        for (String tripUid : tripUids) {
            if (skip) skip = false;
            else sb.append(",");
            sb.append(tripUid);
        }
        this.tripUids = sb.toString();
    }

    public List<TripWithoutDetails> response() {
        return trips;
    }
}
