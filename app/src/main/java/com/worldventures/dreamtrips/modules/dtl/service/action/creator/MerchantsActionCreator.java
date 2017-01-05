package com.worldventures.dreamtrips.modules.dtl.service.action.creator;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.merchants.GetThinMerchantsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ImmutableThinMerchantsActionParams;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.MerchantsActionParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;


public class MerchantsActionCreator implements HttpActionCreator<GetThinMerchantsHttpAction, MerchantsActionParams> {

   @Inject
   public MerchantsActionCreator(){}

   @Override
   public GetThinMerchantsHttpAction createAction(MerchantsActionParams params) {
      return new GetThinMerchantsHttpAction(createMerchantsActionParams(params));
   }

   private static ThinMerchantsActionParams createMerchantsActionParams(MerchantsActionParams bundle) {
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

   public static int calculateOffsetPagination(FilterData filterData) {
      return filterData.page() * filterData.offset();
   }
}
