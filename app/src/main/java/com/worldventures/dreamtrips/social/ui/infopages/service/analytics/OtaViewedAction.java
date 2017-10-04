package com.worldventures.dreamtrips.social.ui.infopages.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "ota_booking",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class OtaViewedAction extends BaseAnalyticsAction {

   public OtaViewedAction() {
   }
}