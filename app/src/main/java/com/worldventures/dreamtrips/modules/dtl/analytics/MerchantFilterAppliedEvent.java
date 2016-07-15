package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;

import java.util.List;
import java.util.Locale;

@AnalyticsEvent(action = "local:Refine Search", trackers = AdobeTracker.TRACKER_KEY)
public class MerchantFilterAppliedEvent extends DtlAnalyticsAction {

    @Attribute("dtlprice")
    final String price;

    @Attribute("dtldistance")
    final String distance;

    @Attribute("amenities")
    final String amenities;

    public MerchantFilterAppliedEvent(DtlFilterData filterData) {
        price = String.format(Locale.US, "%d-%d",
                filterData.getMinPrice(), filterData.getMaxPrice());
        distance = String.format(Locale.US, "10-%.0f%s",
                filterData.getMaxDistance(),
                filterData.getDistanceType().getTypeNameForAnalytics());
        if (filterData.getSelectedAmenities().size() == filterData.getAmenities().size()) {
            amenities = "All";
        } else {
            amenities = joinAmenities(filterData.getSelectedAmenities());
        }
    }

    private static String joinAmenities(List<DtlMerchantAttribute> amenities) {
        if (amenities.size() == 0) return "";
        return Queryable.from(amenities)
                .map(DtlMerchantAttribute::getName)
                .joinStrings(",");
    }
}
