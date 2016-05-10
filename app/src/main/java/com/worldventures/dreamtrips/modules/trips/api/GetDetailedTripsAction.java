package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

//TODO change method and endpoint
@HttpAction(value = "/api/users/profiles/short", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.POST)
public class GetDetailedTripsAction  {

    @Body DetailedTripsBody detailedTripsBody;

    @Response List<TripModel> tripList;

    public GetDetailedTripsAction(List<String> tripUids) {
        detailedTripsBody = new DetailedTripsBody(tripUids);
    }

    public List<TripModel> getTripList() {
        return tripList;
    }

    private class DetailedTripsBody {

        private List<String> tripUids;

        public DetailedTripsBody(List<String> tripUids) {
            this.tripUids = tripUids;
        }
    }
}
