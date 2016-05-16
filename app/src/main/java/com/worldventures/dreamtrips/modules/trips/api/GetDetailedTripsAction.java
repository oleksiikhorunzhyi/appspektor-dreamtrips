package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/trips/details", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.GET)
public class GetDetailedTripsAction extends BaseHttpAction {

    @Query("ids")
    String ids;

    @Response
    List<TripModel> tripList;

    public GetDetailedTripsAction(List<String> tripUids) {
        ids = android.text.TextUtils.join(",", tripUids);

    }

    public List<TripModel> getTripList() {
        return tripList;
    }

}
