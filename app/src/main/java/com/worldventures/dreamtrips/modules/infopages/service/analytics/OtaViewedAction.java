package com.worldventures.dreamtrips.modules.infopages.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "ota_booking",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class OtaViewedAction extends BaseAnalyticsAction {

   public OtaViewedAction() {
   }
}