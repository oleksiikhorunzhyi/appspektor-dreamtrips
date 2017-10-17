package com.worldventures.dreamtrips.social.ui.podcast_player.service;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership:podcasts",
                trackers = {AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY})
public class ViewPodcastAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("view") String view = "1";
}
