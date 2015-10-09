package com.worldventures.dreamtrips.modules.dtl.api;

import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import java.util.ArrayList;

public class GetDtlLocationsQuery extends DtlRequest<ArrayList<DtlLocation>> {

    private double lat;
    private double lng;
    private int rad;

    public GetDtlLocationsQuery(double lat, double lng, int rad) {
        super((Class<ArrayList<DtlLocation>>) new ArrayList<DtlLocation>().getClass());
        this.lat = lat;
        this.lng = lng;
        this.rad = rad;
    }

    @Override
    public ArrayList<DtlLocation> loadDataFromNetwork() {
        ArrayList<DtlLocation> result = getService().getDtlLocations(lat, lng, rad).getCities();
        if (result == null)
            result = new ArrayList<>();
        return result;
    }
}
