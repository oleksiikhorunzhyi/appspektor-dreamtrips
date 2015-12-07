package com.worldventures.dreamtrips.modules.dtl.api.location;

import android.location.Location;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import java.util.ArrayList;

public class GetDtlLocationsQuery extends DtlRequest<ArrayList<DtlLocation>> {

    private String query;
    private String latLng;

    public GetDtlLocationsQuery(Location location) {
        super((Class<ArrayList<DtlLocation>>) new ArrayList<DtlLocation>().getClass());
        this.latLng = new StringBuilder().append(location.getLatitude())
                .append(",").append(location.getLongitude()).toString();
    }

    public GetDtlLocationsQuery(String query) {
        super((Class<ArrayList<DtlLocation>>) new ArrayList<DtlLocation>().getClass());
        this.query = query;
    }

    @Override
    public ArrayList<DtlLocation> loadDataFromNetwork() throws Exception {
        return getService().getNearbyDtlLocations(latLng, query);
    }
}
