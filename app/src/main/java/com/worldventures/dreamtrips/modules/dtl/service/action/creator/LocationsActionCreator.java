package com.worldventures.dreamtrips.modules.dtl.service.action.creator;


import com.worldventures.dreamtrips.api.dtl.locations.LocationsHttpAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.LocationsActionParams;

import java.util.Locale;

import javax.inject.Inject;

public class LocationsActionCreator implements HttpActionCreator<LocationsHttpAction, LocationsActionParams> {

   @Inject
   public LocationsActionCreator(){}

   @Override
   public LocationsHttpAction createAction(LocationsActionParams params) {
      if(params.location() == null && params.query() == null) throw new NullPointerException("Can't create LocationsHttpAction : locations or query must be present");
      return params.location() != null ? createNearbyHttpAction(params.location()) : createLocationSearchHttpAction(params.query());
   }

   public static LocationsHttpAction createNearbyHttpAction(android.location.Location location) {
      return new LocationsHttpAction(null, provideFormattedCoordinates(location));
   }

   public static LocationsHttpAction createLocationSearchHttpAction(String query) {
      return new LocationsHttpAction(query, null);
   }

   private static String provideFormattedCoordinates(android.location.Location location) {
      return String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude());
   }
}
