package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership-enroll-merchant",
                trackers = AdobeTracker.TRACKER_KEY)
public class EnrollMerchantViewedAction extends BaseAnalyticsAction {

   @Attribute("member_id")
   final String memberId;

   @Attribute("view")
   final String view = "1";

   public EnrollMerchantViewedAction(String memberId) {
      this.memberId = memberId;
   }
}
