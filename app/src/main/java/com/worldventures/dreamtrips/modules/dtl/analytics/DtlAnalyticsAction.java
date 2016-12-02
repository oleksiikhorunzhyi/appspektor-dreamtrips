package com.worldventures.dreamtrips.modules.dtl.analytics;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

public abstract class DtlAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("dtllocation") String dtlLocation = null;

   @Attribute("locationmethod") String dtlLocationMethod = null;

   public void setAnalyticsLocation(@NonNull DtlLocation dtlLocation) {
      this.dtlLocation = dtlLocation.analyticsName();
      switch (dtlLocation.locationSourceType()) {
         case NEAR_ME:
            dtlLocationMethod = "Near me";
            break;
         case EXTERNAL:
            dtlLocationMethod = "Search";
            break;
         case FROM_MAP:
            dtlLocationMethod = "Map";
            break;
         default:
            dtlLocationMethod = "Unknown";
      }
   }
}
