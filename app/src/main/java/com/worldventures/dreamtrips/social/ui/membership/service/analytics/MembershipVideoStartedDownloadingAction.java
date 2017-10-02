package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership",
                category = "nav_menu",
                trackers = {ApptentiveTracker.TRACKER_KEY})
public class MembershipVideoStartedDownloadingAction extends BaseAnalyticsAction {

   public MembershipVideoStartedDownloadingAction() {
   }
}
