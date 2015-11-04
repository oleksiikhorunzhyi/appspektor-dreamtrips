package com.worldventures.dreamtrips.modules.dtl.api.location;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import java.util.ArrayList;

public class GetNearbyDtlLocationQuery extends DtlRequest<ArrayList<DtlLocation>> {

    private double latitude;
    private double longitude;

    public GetNearbyDtlLocationQuery(double latitude, double longitude) {
        super((Class<ArrayList<DtlLocation>>) new ArrayList<DtlLocation>().getClass());
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public ArrayList<DtlLocation> loadDataFromNetwork() throws Exception {
        return getService().getNearbyDtlLocations(latitude, longitude);
    }
}
