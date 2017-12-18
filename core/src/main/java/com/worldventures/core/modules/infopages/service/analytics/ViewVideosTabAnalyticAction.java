package com.worldventures.core.modules.infopages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "Help:Videos",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewVideosTabAnalyticAction extends BaseAnalyticsAction {

   @Attribute("language") final String language;

   public ViewVideosTabAnalyticAction(String language) {
      this.language = language;
   }
}
