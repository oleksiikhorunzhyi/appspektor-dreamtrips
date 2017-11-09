package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership:enroll-member",
                category = "nav_menu",
                trackers = {AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY})
public class EnrollMemberViewedAction extends BaseAnalyticsAction {

   @Attribute("member_id") final String memberId;

   @Attribute("view") final String view = "1";

   public EnrollMemberViewedAction(String memberId) {
      this.memberId = memberId;
   }
}
