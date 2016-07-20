package com.worldventures.dreamtrips.modules.dtl.analytics;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

public abstract class DtlAnalyticsAction extends BaseAnalyticsAction {

    @Attribute("dtllocation")
    String dtlLocation;

    @Attribute("locationmethod")
    String dtlLocationMethod;

    public void setAnalyticsLocation(@Nullable DtlLocation dtlLocation) {
        this.dtlLocation = dtlLocation == null ? null : dtlLocation.getAnalyticsName();
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
