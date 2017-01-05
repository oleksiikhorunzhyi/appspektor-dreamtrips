package com.worldventures.dreamtrips.modules.dtl.helper;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;

public class DtlLocationHelper {

   public static final double MAX_DISTANCE = 50;

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
   public static LatLng selectAcceptableLocation(Location deviceLocation, DtlLocation dtlLocation) {
      if (dtlLocation.locationSourceType() == LocationSourceType.EXTERNAL) {
         return dtlLocation.coordinates();
      }
      //
      if (dtlLocation.locationSourceType() == LocationSourceType.NEAR_ME || dtlLocation.locationSourceType() == LocationSourceType.UNDEFINED) {
         return new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
      }
      //
      LatLng deviceLatLng = asLatLng(deviceLocation);
      LatLng cityLatLng = dtlLocation.coordinates();
      return checkLocation(deviceLatLng, cityLatLng, MAX_DISTANCE, DistanceType.MILES) ? deviceLatLng : cityLatLng;
   }

   public static boolean checkMaxDistance(LatLng currentLocation, LatLng targetLatLng) {
      return checkLocation(currentLocation, targetLatLng, MAX_DISTANCE, DistanceType.MILES);
   }

   public static boolean checkMinDistance(LatLng currentLocation, LatLng targetLatLng) {
      return checkLocation(currentLocation, targetLatLng, 0.5, DistanceType.MILES);
   }

   public static boolean checkLocation(LatLng currentLocation, LatLng targetLatLng, double maxDistance, DistanceType distanceType) {
      double distance = distanceType == DistanceType.KMS ? distanceInKms(currentLocation, targetLatLng) : distanceInMiles(currentLocation, targetLatLng);
      return distance < maxDistance;
   }

   public static LatLng asLatLng(Location location) {
      return new LatLng(location.getLatitude(), location.getLongitude());
   }

   public static double calculateDistance(LatLng currentLatLng, LatLng targetLatLng) {
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
      android.location.Location.distanceBetween(targetLocation.latitude, targetLocation.longitude, currentLocation.latitude, currentLocation.longitude, distance);
      return distance[0];
   }
}
