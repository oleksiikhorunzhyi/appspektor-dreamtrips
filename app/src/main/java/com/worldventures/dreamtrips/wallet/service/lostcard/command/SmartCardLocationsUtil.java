package com.worldventures.dreamtrips.wallet.service.lostcard.command;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocation;

import java.util.Collections;
import java.util.List;

public class SmartCardLocationsUtil {

   private SmartCardLocationsUtil() {
   }

   public static SmartCardLocation getLatestLocation(List<SmartCardLocation> smartCardLocations) {
      return Queryable.from(smartCardLocations)
            .distinct()
            .sort((smartCardLocation1, smartCardLocation2) -> smartCardLocation1.createdAt().compareTo(smartCardLocation2.createdAt()))
            .first();
   }
}
