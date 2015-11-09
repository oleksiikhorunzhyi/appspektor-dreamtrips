package com.worldventures.dreamtrips.modules.dtl.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LocationDelegate {

    ReactiveLocationProvider reactiveLocationProvider;

    public LocationDelegate(Context context) {
        reactiveLocationProvider = new ReactiveLocationProvider(context);
    }

    public Subscription getLastKnownLocation(Action1<Location> onLastKnownLocationObtained) {
        return getLastKnownLocation(onLastKnownLocationObtained, () -> {
        });
    }


    public Subscription getLastKnownLocation(Action1<Location> onLastKnownLocationObtained, Action0 onComplete) {
        return reactiveLocationProvider.getLastKnownLocation().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(onLastKnownLocationObtained, throwable -> {
                }, onComplete);
    }

    public Subscription requestLocationUpdates(Action1<Status> onResolutionRequired,
                                               Action1<Location> onLocationObtained,
                                               Action1<Throwable> onLocationError) {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setNumUpdates(1)
                .setInterval(1000);

        return reactiveLocationProvider.checkLocationSettings(
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(request)
                        .setAlwaysShow(true)
                        .build()
        ).doOnNext(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
                onResolutionRequired.call(status);
        }).flatMap(locationSettingsResult -> reactiveLocationProvider.getUpdatedLocation(request)
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onLocationObtained, onLocationError);
    }
}
