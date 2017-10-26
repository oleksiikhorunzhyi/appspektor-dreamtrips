package com.worldventures.wallet.util;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation;

import java.util.List;

public class WalletLocationsUtil {

   private WalletLocationsUtil() {
   }

   public static LatLng toLatLng(WalletCoordinates position) {
      return new LatLng(
            position.lat(),
            position.lng()
      );
   }

   public static WalletLocation getLatestLocation(List<WalletLocation> smartCardLocations) {
      return !smartCardLocations.isEmpty() ? applyQueryToLocationList(smartCardLocations) : null;
   }

   private static WalletLocation applyQueryToLocationList(List<WalletLocation> smartCardLocations) {
      return Queryable.from(smartCardLocations)
            .distinct()
            .sort((smartCardLocation1, smartCardLocation2) -> smartCardLocation1.createdAt()
                  .compareTo(smartCardLocation2.createdAt()))
            .last();
   }
}
