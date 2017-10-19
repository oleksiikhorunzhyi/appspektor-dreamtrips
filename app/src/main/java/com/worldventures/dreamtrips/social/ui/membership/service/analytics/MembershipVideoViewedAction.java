package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership",
                trackers = {AdobeTracker.TRACKER_KEY})
public class MembershipVideoViewedAction extends BaseAnalyticsAction {

   @Attribute("video_id")
   String videoName;

   @Attribute("view") final String view = "1";

   public MembershipVideoViewedAction(String videoName) {
      this.videoName = videoName;
   }
}
