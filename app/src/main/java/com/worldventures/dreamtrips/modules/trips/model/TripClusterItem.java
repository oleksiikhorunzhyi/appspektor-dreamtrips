package com.worldventures.dreamtrips.modules.trips.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class TripClusterItem implements ClusterItem {

    private MapObjectHolder mapObjectHolder;

    public TripClusterItem(MapObjectHolder mapObjectHolder) {
        this.mapObjectHolder = mapObjectHolder;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(mapObjectHolder.getItem().getCoordinates().getLat(), mapObjectHolder.getItem().getCoordinates().getLng());
    }

    public MapObjectHolder getMapObjectHolder() {
        return mapObjectHolder;
    }
}
