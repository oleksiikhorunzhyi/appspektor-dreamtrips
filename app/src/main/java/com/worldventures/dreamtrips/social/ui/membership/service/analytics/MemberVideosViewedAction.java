package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership:videos",
                category = "nav_menu",
                trackers = {AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY})
public class MemberVideosViewedAction extends BaseAnalyticsAction {

   @Attribute("member_id")
   final String memberId;

   public MemberVideosViewedAction(String memberId) {
      this.memberId = memberId;
   }
}
