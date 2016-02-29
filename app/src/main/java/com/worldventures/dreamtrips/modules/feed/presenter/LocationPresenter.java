package com.worldventures.dreamtrips.modules.feed.presenter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.PermissionView;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class LocationPresenter<V extends LocationPresenter.View> extends Presenter<LocationPresenter.View> {

    @Inject
    LocationDelegate gpsLocationDelegate;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.checkPermissions();
    }

    public Observable<Location> getLocation() {
        return view.bind(gpsLocationDelegate
                .getLastKnownLocation())
                .compose(IoToMainComposer.get())
                .map(this::getLocationFromAndroidLocation);
    }

    @NonNull
    private Location getLocationFromAndroidLocation(android.location.Location location) {
        Geocoder coder = new Geocoder(view.getContext(), Locale.ENGLISH);
        Location newLocation = new Location();
        try {
            List<Address> results = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!results.isEmpty()) {
                Address address = results.get(0);
                newLocation.setLat(address.getLatitude());
                newLocation.setLng(address.getLongitude());
                newLocation.setName(address.getCountryName() + " " + address.getLocality());
            }
        } catch (IOException e) {
            Timber.e(e, "");
        }
        return newLocation;
    }

    public void onPermissionGranted() {
        view.bind(gpsLocationDelegate.requestLocationUpdate()
                .compose(IoToMainComposer.get()))
                .subscribe(this::onLocationObtained, this::onLocationError);
    }

    private void onStatusError(Status status) {
        view.resolutionRequired(status);
    }

    private void onLocationError(Throwable e) {
        if (e instanceof LocationDelegate.LocationException)
            onStatusError(((LocationDelegate.LocationException) e).getStatus());
        else locationNotGranted();
    }

    private void onLocationObtained(android.location.Location location) {
        gpsLocationDelegate.onLocationObtained(location);
    }

    public void locationNotGranted() {
        gpsLocationDelegate.onLocationObtained(null);
    }

    public interface View extends RxView, PermissionView {

        void resolutionRequired(Status status);

        Context getContext();
    }
}
