package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership",
                trackers = {AdobeTracker.TRACKER_KEY})
public class MembershipVideoDownloadedAction extends BaseAnalyticsAction {

   @Attribute("video_id")
   String videoName;

   @Attribute("download") final String download = "1";

   public MembershipVideoDownloadedAction(String videoName) {
      this.videoName = videoName;
   }
}
