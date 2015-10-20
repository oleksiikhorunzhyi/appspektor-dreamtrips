package com.worldventures.dreamtrips.modules.dtl.api;

import com.worldventures.dreamtrips.modules.dtl.model.DtlLocationsHolder;

public class GetDtlLocationsQuery extends DtlRequest<DtlLocationsHolder> {

    private double lat;
    private double lng;

    public GetDtlLocationsQuery(double lat, double lng) {
        super(DtlLocationsHolder.class);
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public DtlLocationsHolder loadDataFromNetwork() {
        return getService().getDtlLocations(lat, lng);
    }
}
