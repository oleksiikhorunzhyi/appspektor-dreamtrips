package com.worldventures.dreamtrips.modules.dtl.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

public class LocationDelegate {

    ReactiveLocationProvider reactiveLocationProvider;

    public LocationDelegate(Context context) {
        reactiveLocationProvider = new ReactiveLocationProvider(context);
    }

    public Observable<Location> getLastKnownLocation() {
        return reactiveLocationProvider.getLastKnownLocation();
    }

    public Observable<LocationSettingsResult> checkSettings() {
        return reactiveLocationProvider.checkLocationSettings(new LocationSettingsRequest.Builder()
                .addLocationRequest(provideLocationRequest())
                .setAlwaysShow(true)
                .build());
    }

    public Observable<Location> requestLocationUpdate() {
        return reactiveLocationProvider.getUpdatedLocation(provideLocationRequest());
    }

    private LocationRequest provideLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setNumUpdates(1)
                .setInterval(1000);
    }

}
