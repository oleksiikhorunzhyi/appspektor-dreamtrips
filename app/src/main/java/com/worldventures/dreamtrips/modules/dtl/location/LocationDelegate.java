package com.worldventures.dreamtrips.modules.dtl.location;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.innahema.collections.query.queriables.Queryable;

import java.util.ArrayList;
import java.util.List;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import timber.log.Timber;

public class LocationDelegate {

    ReactiveLocationProvider reactiveLocationProvider;

    private PermissionView permissionView;
    private List<LocationListener> listeners = new ArrayList<>();

    public LocationDelegate(Context context) {
        reactiveLocationProvider = new ReactiveLocationProvider(context);
    }

    public void setPermissionView(PermissionView permissionView) {
        this.permissionView = permissionView;
    }

    public void dropPermissionView() {
        this.permissionView = null;
    }

    public void tryRequestLocation() {
        if (permissionView == null) {
            Timber.e("permissionView can not be null at this point! Check your setup!");
            return;
        }
        //
        permissionView.checkPermissions();
    }

    public void attachListener(LocationListener locationListener) {
        listeners.add(locationListener);
    }

    public void detachListener(LocationListener locationListener) {
        listeners.remove(locationListener);
    }

    public void onLocationObtained(@Nullable Location location) {
        Queryable.from(listeners).forEachR(listener -> listener.onLocationObtained(location));
    }

    public Observable<Location> getLastKnownLocation() {
        return reactiveLocationProvider.getLastKnownLocation()
                .switchIfEmpty(requestLocationUpdate());
    }

    public Observable<Location> getLastKnownLocationOrEmpty() {
        return reactiveLocationProvider.getLastKnownLocation();
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

    public interface LocationListener {
        void onLocationObtained(Location location);
    }
}
