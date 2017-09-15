package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "360_videos",
                trackers = {AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY})
public class TripImageVideo360DownloadedAction extends BaseAnalyticsAction {

   @Attribute("video_id")
   String videoName;

   @Attribute("download")
   String download="1";

   public TripImageVideo360DownloadedAction(String videoName) {
      this.videoName = videoName;
   }
}
