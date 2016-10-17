package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;

import java.util.List;
import java.util.Locale;

@AnalyticsEvent(action = "local:Refine Search", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantFilterAppliedEvent extends DtlAnalyticsAction {

   @com.worldventures.dreamtrips.core.utils.tracksystem.Attribute("dtlprice") final String price;

   @com.worldventures.dreamtrips.core.utils.tracksystem.Attribute("dtldistance") final String distance;

   @com.worldventures.dreamtrips.core.utils.tracksystem.Attribute("amenities") final String amenities;

   public MerchantFilterAppliedEvent(FilterData filterData) {
      price = String.format(Locale.US, "%d-%d", filterData.budgetMin(), filterData.budgetMax());
      distance = String.format(Locale.US, "%.0f-%.0f%s",
            FilterHelper.provideLeftDistanceValue(filterData),
            FilterHelper.provideMaxDistance(filterData),
            filterData.distanceType().getTypeNameForAnalytics());
      if (filterData.selectedAmenities().isEmpty()) {
         amenities = "All";
      } else {
         amenities = joinAmenities(filterData.selectedAmenities());
      }
   }

   private static String joinAmenities(List<Attribute> amenities) {
      return Queryable.from(amenities).map(Attribute::displayName).joinStrings(",");
   }
}
