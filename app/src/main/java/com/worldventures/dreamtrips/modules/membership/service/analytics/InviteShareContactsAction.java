package com.worldventures.dreamtrips.modules.membership.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "invite_share_select_contacts",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class InviteShareContactsAction extends BaseAnalyticsAction {

   public InviteShareContactsAction() {
   }
}
