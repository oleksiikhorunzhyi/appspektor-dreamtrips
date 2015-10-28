package com.worldventures.dreamtrips.core.utils;

import com.google.android.gms.maps.model.LatLng;

public class LocationHelper {

    public static boolean checkLocation(int maxDistance, LatLng currentLocation, LatLng targetLocation) {
        float[] distance = new float[1];
        android.location.Location.distanceBetween(targetLocation.latitude, targetLocation.longitude,
                currentLocation.latitude, currentLocation.longitude, distance);
        double distanceInMiles = 0.000621371d * distance[0];
        return distanceInMiles < maxDistance;
    }

}
