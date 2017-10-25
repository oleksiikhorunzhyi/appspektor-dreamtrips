package com.worldventures.dreamtrips.social.ui.podcast_player.service;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership:podcasts:download",
                trackers = AdobeTracker.TRACKER_KEY)
public class PodcastDownloadedAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("pcname") String podcastName;
   @Attribute("pcdownload") String downloaded = "1";

   public PodcastDownloadedAnalyticsAction(String podcastName) {
      this.podcastName = "dta:" + podcastName;
   }
}
