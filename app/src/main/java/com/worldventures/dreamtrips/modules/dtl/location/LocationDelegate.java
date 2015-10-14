package com.worldventures.dreamtrips.modules.dtl.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationDelegate {

    private LocationManager locationManager;

    public LocationDelegate(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLastKnownLocation() {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }
}
