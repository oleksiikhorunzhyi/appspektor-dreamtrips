package com.worldventures.dreamtrips.modules.dtl.api.location;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import java.util.ArrayList;

public class GetDtlLocationsQuery extends DtlRequest<ArrayList<DtlLocation>> {

    private String query;
    private String latLng;

    public GetDtlLocationsQuery(double latitude, double longitude) {
        super((Class<ArrayList<DtlLocation>>) new ArrayList<DtlLocation>().getClass());
        this.latLng = new StringBuilder().append(latitude).append(",").append(longitude).toString();
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
