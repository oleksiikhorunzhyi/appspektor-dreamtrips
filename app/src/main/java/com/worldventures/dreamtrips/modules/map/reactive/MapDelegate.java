package com.worldventures.dreamtrips.modules.map.reactive;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.techery.spares.module.Injector;

import rx.Observable;

public class MapDelegate {

    public MapDelegate(Injector injector) {
        injector.inject(this);
    }

    public Observable<CameraPosition> subscribeToCameraChange(GoogleMap googleMap) {
        return CameraChangeObservable.create(googleMap);
    }

    public Observable<Marker> subscribeToMarkerClick(GoogleMap googleMap) {
        return MarkerClickObservable.create(googleMap);
    }

}
