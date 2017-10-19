package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "360_videos",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageVideo360DownloadedAction extends BaseAnalyticsAction {

   @Attribute("video_id")
   String videoName;

   @Attribute("download")
   String download = "1";

   public TripImageVideo360DownloadedAction(String videoName) {
      this.videoName = videoName;
   }
}
