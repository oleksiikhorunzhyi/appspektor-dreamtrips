package com.worldventures.dreamtrips.modules.map.reactive;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;

import rx.Observable;

public class MapObservableFactory {

    private MapObservableFactory() {
        throw new RuntimeException("No instance");
    }

    public static Observable<CameraPosition> createCameraChangeObservable(GoogleMap map) {
        return CameraChangeObservable.create(map);
    }

    public static Observable<Marker> createMarkerClickObservable(GoogleMap map) {
        return MarkerClickObservable.create(map);
    }
}
