package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Calculate Points",
                trackers = AdobeTracker.TRACKER_KEY)
public class PointsEstimatorCalculateEvent extends MerchantAnalyticsAction {

   @Attribute("calcpoints") final String attribute = "1";

   public PointsEstimatorCalculateEvent(MerchantAttributes merchantAttributes) {
      super(merchantAttributes);
   }
}
