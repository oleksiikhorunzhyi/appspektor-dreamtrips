package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Point Estimator",
                trackers = AdobeTracker.TRACKER_KEY)
public class PointsEstimatorViewEvent extends MerchantAnalyticsAction {

   @Attribute("pointest") final String attribute = "1";

   public PointsEstimatorViewEvent(MerchantAttributes merchantAttributes) {
      super(merchantAttributes);
   }
}
