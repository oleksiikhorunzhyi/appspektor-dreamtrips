package com.worldventures.dreamtrips.modules.map.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

public class DtlClusterItem implements ClusterItem {

    private final LatLng latLng;
    private final String id;
    private DtlPlaceType dtlPlaceType;

    public DtlClusterItem(String id, LatLng latLng, DtlPlaceType dtlPlaceType) {
        this.id = id;
        this.latLng = latLng;
        this.dtlPlaceType = dtlPlaceType;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public String getId() {
        return id;
    }

    public DtlPlaceType getDtlPlaceType() {
        return dtlPlaceType;
    }
}
