package com.worldventures.dreamtrips.modules.trips.manager.functions;

import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.Coordinates;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class RemoveOldMarkersAction implements Action1<List<Pair<Bitmap, MapObject>>> {

    private final List<Marker> existsMarkers;

    public RemoveOldMarkersAction(List<Marker> existsMarkers) {
        this.existsMarkers = existsMarkers;
    }

    @Override
    public void call(List<Pair<Bitmap, MapObject>> pairs) {
        List<Marker> markersToRemove = new ArrayList<>();
        Queryable.from(existsMarkers).forEachR(marker -> {
            if (Queryable.from(pairs).firstOrDefault(pair -> {
                Coordinates coordinates = pair.second.getCoordinates();
                LatLng latLng = new LatLng(coordinates.getLat(), coordinates.getLng());
                return marker.getPosition().equals(latLng);
            }) == null) {
                markersToRemove.add(marker);
                marker.remove();
            }
        });
        existsMarkers.removeAll(markersToRemove);
    }
}
