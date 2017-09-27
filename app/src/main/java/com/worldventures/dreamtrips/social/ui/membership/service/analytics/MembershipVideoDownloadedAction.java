package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership",
                trackers = {AdobeTracker.TRACKER_KEY})
public class MembershipVideoDownloadedAction extends BaseAnalyticsAction {

   @Attribute("video_id")
   String videoName;

   @Attribute("download")
   final String download = "1";

   public MembershipVideoDownloadedAction(String videoName) {
      this.videoName = videoName;
   }
}
