package com.worldventures.dreamtrips.modules.version_check.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "new version available", trackers = AdobeTracker.TRACKER_KEY)
public class UpdateAppAction extends BaseAnalyticsAction {

   @Attribute("newver") String newVersion;

   public UpdateAppAction(String newVersion) {
      this.newVersion = newVersion;
   }
}
