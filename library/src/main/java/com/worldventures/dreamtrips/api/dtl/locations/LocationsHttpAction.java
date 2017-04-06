package com.worldventures.dreamtrips.api.dtl.locations;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.locations.model.Location;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/dtl/v2/locations")
public class LocationsHttpAction extends AuthorizedHttpAction {

    @Query("query")
    String query;
    @Query("ll")
    String coordinates;

    @Response
    List<Location> locations;

    public LocationsHttpAction(String query, String coordinates) {
        this.query = query;
        this.coordinates = coordinates;
    }

    public List<Location> locations() {
        return locations;
    }
}
