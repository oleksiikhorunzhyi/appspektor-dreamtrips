package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.janet.analytics.AnalyticsEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

import static com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper.MILES_MULTIPLIER;

@AnalyticsEvent(action = "local:Refine Search", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantFilterAppliedEvent extends DtlAnalyticsAction {

   @com.worldventures.core.service.analytics.Attribute("dtlprice") final String price;

   @com.worldventures.core.service.analytics.Attribute("dtldistance") final String distance;

   @com.worldventures.core.service.analytics.Attribute("amenities") final String amenities;

   public MerchantFilterAppliedEvent(FilterData filterData) {
      price = String.format(Locale.US, "%d-%d", filterData.budgetMin(), filterData.budgetMax());
      distance = String.format(Locale.US, "%.0f-%.0f%s",
            FilterHelper.provideLeftDistanceValue(filterData),
            formatMaxDistance(filterData),
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

   @SuppressWarnings("PMD.AvoidDecimalLiteralsInBigDecimalConstructor")
   private static double formatMaxDistance(FilterData filterData) {
      double valueInKms = FilterHelper.provideMaxDistance(filterData);
      if (filterData.distanceType() == DistanceType.MILES) {
         valueInKms = valueInKms / MILES_MULTIPLIER;
      }
      return new BigDecimal(valueInKms).setScale(1, RoundingMode.DOWN).doubleValue();
   }
}
