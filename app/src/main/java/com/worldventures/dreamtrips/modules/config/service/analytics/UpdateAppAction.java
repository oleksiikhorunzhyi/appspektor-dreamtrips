package com.worldventures.dreamtrips.modules.config.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "new version available", trackers = AdobeTracker.TRACKER_KEY)
public class UpdateAppAction extends BaseAnalyticsAction {

   @Attribute("newver") String newVersion;

   public UpdateAppAction(String newVersion) {
      this.newVersion = newVersion;
   }
}
