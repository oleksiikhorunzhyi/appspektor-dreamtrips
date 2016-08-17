package com.worldventures.dreamtrips.modules.dtl.helper;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;

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
   public static LatLng selectAcceptableLocation(Location deviceLocation, DtlLocation dtlLocation) {
      if (dtlLocation.getLocationSourceType() == LocationSourceType.EXTERNAL) {
         return dtlLocation.getCoordinates().asLatLng();
      }
      //
      if (dtlLocation.getLocationSourceType() == LocationSourceType.NEAR_ME || dtlLocation.getLocationSourceType() == LocationSourceType.UNDEFINED) {
         return new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
      }
      //
      LatLng deviceLatLng = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
      LatLng cityLatLng = dtlLocation.getCoordinates().asLatLng();
      return checkLocation(DtlFilterParameters.MAX_DISTANCE, deviceLatLng, cityLatLng, DistanceType.MILES) ? deviceLatLng : cityLatLng;
   }

   public static boolean checkLocation(double maxDistance, LatLng currentLocation, LatLng targetLatLng, DistanceType distanceType) {
      double distance = distanceType == DistanceType.KMS ? distanceInKms(currentLocation, targetLatLng) : distanceInMiles(currentLocation, targetLatLng);
      return distance < maxDistance;
   }

   public static boolean checkLocation(double maxDistance, Location currentLocation, Location targetLatLng, DistanceType distanceType) {
      LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
      LatLng target = new LatLng(targetLatLng.getLatitude(), targetLatLng.getLongitude());
      //
      return checkLocation(maxDistance, current, target, distanceType);
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
