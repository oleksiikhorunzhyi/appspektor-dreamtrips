package com.worldventures.dreamtrips.modules.dtl.helper;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;

public class DtlLocationHelper {

    /**
     * Check if deviceLocation in near enough to city center
     * <p>
     * if it is, returns back LatLng of device
     * else return cityLatLng
     *
     * @param deviceLocation current location of device
     * @param dtlLocation    selected dtl location object
     * @return LatLng object preferable for filtering purposes
     */
    public LatLng getAcceptedLocation(Location deviceLocation, DtlLocation dtlLocation) {
        LatLng deviceLatLng = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
        LatLng cityLatLng = dtlLocation.getCoordinates().asLatLng();

        return checkLocation(DtlFilterData.MAX_DISTANCE, deviceLatLng, cityLatLng, DtlFilterData.DistanceType.MILES)
                ? deviceLatLng
                : cityLatLng;
    }

    public boolean checkLocation(int maxDistance, LatLng currentLocation, LatLng targetLatLng,
                                 DtlFilterData.DistanceType distanceType) {
        double distance = distanceType == DtlFilterData.DistanceType.KMS
                ? distanceInKms(currentLocation, targetLatLng)
                : distanceInMiles(currentLocation, targetLatLng);
        return distance < maxDistance;
    }

    public double calculateDistance(LatLng currentLatLng,
                                    LatLng targetLatLng) {
        return distance(currentLatLng, targetLatLng);
    }

    public static double distanceInMiles(LatLng currentLocation, LatLng targetLocation) {
        return metresToMiles(distance(currentLocation, targetLocation));
    }

    public static double distanceInKms(LatLng currentLocation, LatLng targetLocation) {
        return metresToKilometers(distance(currentLocation, targetLocation));
    }

    public static double metresToMiles(double distance) {
        return 0.000621371d * distance;
    }

    public static double metresToKilometers(double distance) {
        return 0.001d * distance;
    }

    public static double distance(LatLng currentLocation, LatLng targetLocation) {
        float[] distance = new float[1];
        android.location.Location.distanceBetween(targetLocation.latitude, targetLocation.longitude,
                currentLocation.latitude, currentLocation.longitude, distance);
        return distance[0];
    }

}
