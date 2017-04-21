package com.worldventures.dreamtrips.modules.infopages.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Help:Videos",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewVideosTabAnalyticAction extends BaseAnalyticsAction {

   @Attribute("language") final String language;

   public ViewVideosTabAnalyticAction(String language) {
      this.language = language;
   }
}
