package com.worldventures.dreamtrips.modules.dtl.service.action;


import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.locations.LocationsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.ThinMerchantsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ImmutableThinMerchantsActionParams;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.MerchantsParamsBundle;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HttpActionsCreator {

   public static ThinMerchantsHttpAction provideMerchantsAction(MerchantsParamsBundle bundle) {
      return new ThinMerchantsHttpAction(provideMerchantsActionParams(bundle));
   }

   public static LocationsHttpAction provideNearbyHttpAction(android.location.Location location) {
      if (location == null) throw new NullPointerException("Could not create LocationsHttpAction : location is null");
      return new LocationsHttpAction(null, provideFormattedCoordinates(location));
   }

   public static LocationsHttpAction provideLocationSearchHttpAction(String query) {
      if (query == null) throw new NullPointerException("Could not create LocationsHttpAction : query is null");
      return new LocationsHttpAction(query, null);
   }

   private static ThinMerchantsActionParams provideMerchantsActionParams(MerchantsParamsBundle bundle) {
      final FilterData filterData = bundle.filterData();
      final DtlLocation location = bundle.location();
      return ImmutableThinMerchantsActionParams.builder()
            .coordinates(location.provideFormattedLocation())
            .radius(FilterHelper.provideMaxDistance(filterData))
            .search(TextUtils.isEmpty(filterData.searchQuery()) ? null : filterData.searchQuery())
            .partnerStatuses(providePartnerStatusParameter(filterData.isOffersOnly()))
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

   private static List<String> providePartnerStatusParameter(boolean isOffersOnly) {
      return isOffersOnly ? Arrays.asList(PartnerStatus.PARTICIPANT.toString().toLowerCase(Locale.US),
            PartnerStatus.PENDING.toString().toLowerCase(Locale.US)) : null;
   }

   private static List<String> provideAmenitiesParameter(FilterData filterData) {
      if (filterData.selectedAmenities().isEmpty()) return null;

      final String parameter = "amenity:" +
            Queryable.from(filterData.selectedAmenities()).map(Attribute::id).joinStrings(";");
      return new ArrayList<>(Arrays.asList(parameter));
   }

   private static String provideFormattedCoordinates(android.location.Location location) {
      return String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude());
   }
}
