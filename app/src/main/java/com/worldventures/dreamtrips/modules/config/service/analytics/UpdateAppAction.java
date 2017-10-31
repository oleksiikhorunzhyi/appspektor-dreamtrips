package com.worldventures.dreamtrips.modules.config.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.janet.analytics.AnalyticsEvent;

//import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "new version available", trackers = AdobeTracker.TRACKER_KEY)
public class UpdateAppAction extends BaseAnalyticsAction {

   @Attribute("newver") String newVersion;

   public UpdateAppAction(String newVersion) {
      this.newVersion = newVersion;
   }
}
