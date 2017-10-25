package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "training_videos",
                trackers = {AdobeTracker.TRACKER_KEY})
public class AdobeTrainingVideosViewedAction extends BaseAnalyticsAction {

   @Attribute("list")
   String attribute = "1";
}
