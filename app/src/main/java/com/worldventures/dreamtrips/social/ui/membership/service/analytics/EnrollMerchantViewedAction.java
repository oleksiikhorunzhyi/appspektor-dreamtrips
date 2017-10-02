package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership-enroll-merchant",
                trackers = AdobeTracker.TRACKER_KEY)
public class EnrollMerchantViewedAction extends BaseAnalyticsAction {

   @Attribute("member_id") final String memberId;

   @Attribute("view") final String view = "1";

   public EnrollMerchantViewedAction(String memberId) {
      this.memberId = memberId;
   }
}
