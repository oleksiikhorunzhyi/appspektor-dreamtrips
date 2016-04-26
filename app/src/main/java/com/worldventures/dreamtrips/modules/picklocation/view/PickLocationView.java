package com.worldventures.dreamtrips.modules.picklocation.view;

import android.location.Location;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface PickLocationView extends MvpView {

    void initMap();

    boolean isCurrentLocationSet();

    void setCurrentLocation(Location location, boolean animated);

    void showPlayServicesAbsentOverlay();

    void showRationalForLocationPermission();

    void showDeniedLocationPermissionError();

    void showObtainLocationError();
}
