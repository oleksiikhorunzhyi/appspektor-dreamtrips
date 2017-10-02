package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "invite_share_select_contacts",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class InviteShareContactsAction extends BaseAnalyticsAction {

   public InviteShareContactsAction() {
   }
}
