package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:invite_share",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class InviteShareTemplateAction extends BaseAnalyticsAction {

   public InviteShareTemplateAction() {
   }
}
