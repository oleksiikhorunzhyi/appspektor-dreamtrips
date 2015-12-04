package com.worldventures.dreamtrips.core.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;

public class LocationHelper {

    public static LatLng getAcceptedLocation(Location deviceLocation, DtlLocation cityLocation) {
        LatLng deviceLatLng = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
        LatLng cityLatLng = cityLocation.getCoordinates().asLatLng();

        return checkLocation(DtlFilterData.MAX_DISTANCE, deviceLatLng, cityLatLng, DtlFilterData.DistanceType.MILES)
                ? deviceLatLng : cityLatLng;
    }

    public static boolean checkLocation(int maxDistance, LatLng currentLocation, LatLng targetLocation,
                                        DtlFilterData.DistanceType distanceType) {
        return (distanceType == DtlFilterData.DistanceType.KMS ?
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
