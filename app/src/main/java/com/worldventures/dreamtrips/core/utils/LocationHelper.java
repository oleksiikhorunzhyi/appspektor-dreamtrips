package com.worldventures.dreamtrips.core.utils;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl.model.DtlFilterData;

public class LocationHelper {

    public static boolean checkLocation(int maxDistance, LatLng currentLocation, LatLng targetLocation, DtlFilterData.Distance distance) {
        return (distance == DtlFilterData.Distance.KMS ?
                distanceInKms(currentLocation, targetLocation)
                : distanceInMiles(currentLocation, targetLocation)) < maxDistance;
    }

    public static double distanceInMiles(LatLng currentLocation, LatLng targetLocation) {
        return 0.000621371d * distance(currentLocation, targetLocation);
    }

    public static double distanceInKms(LatLng currentLocation, LatLng targetLocation) {
        return 0.001d * distance(currentLocation, targetLocation);
    }


    public static double distance(LatLng currentLocation, LatLng targetLocation) {
        float[] distance = new float[1];
        android.location.Location.distanceBetween(targetLocation.latitude, targetLocation.longitude,
                currentLocation.latitude, currentLocation.longitude, distance);
        return distance[0];
    }


}
