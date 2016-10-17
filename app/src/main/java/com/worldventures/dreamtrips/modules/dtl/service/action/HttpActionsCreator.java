package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.merchants.ThinMerchantsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ImmutableThinMerchantsActionParams;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HttpActionsCreator {

   public static ThinMerchantsHttpAction provideMerchantsAction(FilterData filterData, DtlLocation location) {
      return new ThinMerchantsHttpAction(provideMerchantsActionParams(filterData, location));
   }

   private static ThinMerchantsActionParams provideMerchantsActionParams(FilterData filterData, DtlLocation dtlLocation) {
      return ImmutableThinMerchantsActionParams.builder()
            .coordinates(provideFormattedLocation(dtlLocation))
            .radius(FilterHelper.provideMaxDistance(filterData))
            .search(TextUtils.isEmpty(filterData.searchQuery()) ? null : filterData.searchQuery())
            .partnerStatus(providePartnerStatusParameter(filterData.isOffersOnly()))
            .budgetMin(filterData.budgetMin())
            .budgetMax(filterData.budgetMax())
            .filterAttributes(provideAmenitiesParameter(filterData))
            .offset(calculateOffsetPagination(filterData))
            .limit(filterData.offset())
            .build();
   }

   public static int calculateOffsetPagination(FilterData filterData) {
      return filterData.page() * filterData.offset();
   }

   private static String provideFormattedLocation(DtlLocation location) {
      final Location coordinates = location.getCoordinates();
      return String.format(Locale.US, "%1$f,%2$f", coordinates.getLat(), coordinates.getLng());
   }

   private static String providePartnerStatusParameter(boolean isOffersOnly) {
      return isOffersOnly ? PartnerStatus.PARTICIPANT + "," + PartnerStatus.PENDING : null;
   }

   private static List<String> provideAmenitiesParameter(FilterData filterData) {
      if (filterData.selectedAmenities() == null || filterData.selectedAmenities().isEmpty()) return null;

      final String parameter = "AMENITY:" +
            Queryable.from(filterData.selectedAmenities()).map(Attribute::id).joinStrings(",");
      return new ArrayList<>(Arrays.asList(parameter));
   }
}
