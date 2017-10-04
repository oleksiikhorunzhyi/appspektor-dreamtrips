package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "training_videos",
                trackers = {AdobeTracker.TRACKER_KEY})
public class AdobeTrainingVideosViewedAction extends BaseAnalyticsAction {

   @Attribute("list")
   String attribute = "1";
}
