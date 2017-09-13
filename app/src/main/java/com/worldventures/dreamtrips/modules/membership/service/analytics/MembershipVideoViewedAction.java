package com.worldventures.dreamtrips.modules.membership.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership",
                trackers = {AdobeTracker.TRACKER_KEY})
public class MembershipVideoViewedAction extends BaseAnalyticsAction {

   @Attribute("video_id")
   String videoName;

   @Attribute("view")
   final String view = "1";

   public MembershipVideoViewedAction(String videoName) {
      this.videoName = videoName;
   }
}
