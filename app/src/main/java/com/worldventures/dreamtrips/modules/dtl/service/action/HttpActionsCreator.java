package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.dtl.merchants.ThinMerchantsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ImmutableThinMerchantsActionParams;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Locale;

public class HttpActionsCreator {

   public static ThinMerchantsHttpAction provideMerchantsAction(FilterData filterData, DtlLocation location) {
      return new ThinMerchantsHttpAction(provideMerchantsActionParams(filterData, location));
   }

   private static ThinMerchantsActionParams provideMerchantsActionParams(FilterData filterData, DtlLocation dtlLocation) {
      return ImmutableThinMerchantsActionParams.builder()
            .radius(FilterHelper.provideDistanceByIndex(filterData.distanceType(), filterData.distanceMaxIndex()))
            .coordinates(provideFormattedLocation(dtlLocation))
            .offset(calculateOffsetPagination(filterData))
            .limit(filterData.offset())
            .search(filterData.searchQuery())
            .build();
   }

   public static int calculateOffsetPagination(FilterData filterData) {
      return filterData.page() * filterData.offset();
   }

   private static String provideFormattedLocation(DtlLocation location) {
      final Location coordinates = location.getCoordinates();
      return String.format(Locale.US, "%1$f,%2$f", coordinates.getLat(), coordinates.getLng());
   }
}
