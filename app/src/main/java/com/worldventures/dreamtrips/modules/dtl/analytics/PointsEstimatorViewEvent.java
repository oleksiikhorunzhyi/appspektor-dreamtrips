package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

@AnalyticsEvent(action = "local:Restaurant-Listings:Merchant View:Point Estimator",
                trackers = AdobeTracker.TRACKER_KEY)
public class PointsEstimatorViewEvent extends MerchantAnalyticsAction {

   @Attribute("pointest") final String attribute = "1";

   public PointsEstimatorViewEvent(Merchant dtlMerchant) {
      super(dtlMerchant);
   }
}
