package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:training_video",
                trackers = {AdobeTracker.TRACKER_KEY})
public class ReptoolsVideoDownloadedAction extends BaseAnalyticsAction {

   @Attribute("video_id")
   String videoName;

   @Attribute("download") final String download = "1";

   public ReptoolsVideoDownloadedAction(String videoName) {
      this.videoName = videoName;
   }
}
