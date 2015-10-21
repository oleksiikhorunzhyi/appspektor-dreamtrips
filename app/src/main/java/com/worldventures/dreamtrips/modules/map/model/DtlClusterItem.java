package com.worldventures.dreamtrips.modules.map.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class DtlClusterItem implements ClusterItem {

    private final LatLng latLng;
    private final int id;

    public DtlClusterItem(LatLng latLng, int id) {
        this.latLng = latLng;
        this.id = id;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public int getId() {
        return id;
    }
}
