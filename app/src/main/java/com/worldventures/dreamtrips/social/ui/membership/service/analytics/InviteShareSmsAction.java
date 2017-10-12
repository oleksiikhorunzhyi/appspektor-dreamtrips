package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "invite_share_send_sms",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class InviteShareSmsAction extends BaseAnalyticsAction {

   public InviteShareSmsAction() {
   }
}
