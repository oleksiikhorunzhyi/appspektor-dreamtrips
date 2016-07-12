package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

public abstract class DtlAnalyticsAction extends BaseAnalyticsAction {

    @Attribute("dtllocation")
    String dtlLocation;

    @Attribute("locationmethod")
    String dtlLocationMethod;

    public void setAnalyticsLocation(DtlLocation dtlLocation) {
        this.dtlLocation = dtlLocation.getLocationSourceType() == LocationSourceType.EXTERNAL ?
                dtlLocation.getAnalyticsName() : null;
        switch (dtlLocation.getLocationSourceType()) {
            case NEAR_ME:
                dtlLocationMethod = "Near me";
                break;
            case EXTERNAL:
                dtlLocationMethod = "Search";
                break;
            case FROM_MAP:
                dtlLocationMethod = "Map";
                break;
            default: dtlLocationMethod = "Unknown";
        }
    }
}
