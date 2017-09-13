package com.worldventures.dreamtrips.modules.reptools.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:training_video",
                trackers = {AdobeTracker.TRACKER_KEY})
public class ReptoolsVideoViewedAction extends BaseAnalyticsAction {

   @Attribute("video_id")
   String videoName;

   @Attribute("view")
   final String view = "1";

   public ReptoolsVideoViewedAction(String videoName) {
      this.videoName = videoName;
   }
}
