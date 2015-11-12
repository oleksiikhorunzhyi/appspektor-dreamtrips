package com.worldventures.dreamtrips.modules.dtl.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

public class LocationDelegate {

    ReactiveLocationProvider reactiveLocationProvider;

    public LocationDelegate(Context context) {
        reactiveLocationProvider = new ReactiveLocationProvider(context);
    }

    public Observable<Location> getLastKnownLocation() {
        return reactiveLocationProvider.getLastKnownLocation()
                .switchIfEmpty(requestLocationUpdate());
    }

    public Observable<Location> requestLocationUpdate() {
        return checkSettings().flatMap(this::settingsResultObtained);
    }

    private Observable<LocationSettingsResult> checkSettings() {
        return reactiveLocationProvider.checkLocationSettings(new LocationSettingsRequest.Builder()
                .addLocationRequest(provideLocationRequest())
                .setAlwaysShow(true)
                .build());
    }

    private Observable<Location> settingsResultObtained(LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
            return Observable.error(new LocationException(status));
        return reactiveLocationProvider.getUpdatedLocation(provideLocationRequest());
    }

    private LocationRequest provideLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setNumUpdates(1)
                .setInterval(1000);
    }

    public static class LocationException extends Exception {
        Status status;

        public LocationException(Status status) {
            this.status = status;
        }

        public Status getStatus() {
            return status;
        }
    }
}
