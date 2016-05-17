package com.worldventures.dreamtrips.modules.dtl.action;

import android.location.Location;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/dtl/v2/locations")
public class DtlNearbyLocationAction extends AuthorizedHttpAction {

    @Query("ll")
    String latLng;

    @Response
    List<DtlExternalLocation> response;

    public DtlNearbyLocationAction(Location location) {
        this.latLng = location.getLatitude() + ","
                + location.getLongitude();
    }

    public List<DtlExternalLocation> getResult() {
        return response;
    }
}
