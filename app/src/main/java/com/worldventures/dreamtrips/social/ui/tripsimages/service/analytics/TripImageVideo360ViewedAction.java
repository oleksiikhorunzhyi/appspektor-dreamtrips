package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "360_videos",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageVideo360ViewedAction extends BaseAnalyticsAction {

   @Attribute("video_id")
   String videoName;

   @Attribute("view") final String view = "1";

   public TripImageVideo360ViewedAction(String videoName) {
      this.videoName = videoName;
   }
}
