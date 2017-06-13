package com.worldventures.dreamtrips.modules.player.service;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership:podcasts:download",
                trackers = AdobeTracker.TRACKER_KEY)
public class PodcastDownloadedAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("pcname") String podcastName;
   @Attribute("pcdownload") String downloaded = "1";

   public PodcastDownloadedAnalyticsAction(String podcastName) {
      this.podcastName = "dta:" + podcastName;
   }
}
