package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Analytics;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

@Analytics(category = TrackingHelper.DTL_ACTION_OFFERS_TAB)
public class MerchantsTabEvent {

    @Attribute(TrackingHelper.DTL_LOCATION)
    String location;

    @Attribute(TrackingHelper.DTL_LOCATION_METHOD)
    String locationMethod;

    public MerchantsTabEvent(DtlLocation dtlLocation) {
        location = dtlLocation.getAnalyticsName();
        switch (dtlLocation.getLocationSourceType()) {
            case NEAR_ME:
                locationMethod = "Near me";
                break;
            case EXTERNAL:
                locationMethod = "Search";
                break;
            case FROM_MAP:
                locationMethod = "Map";
                break;
        }
    }
}
