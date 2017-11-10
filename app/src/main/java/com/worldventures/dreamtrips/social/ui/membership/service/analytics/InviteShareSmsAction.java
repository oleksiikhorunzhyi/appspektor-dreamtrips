package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "invite_share_send_sms",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class InviteShareSmsAction extends BaseAnalyticsAction {

   public InviteShareSmsAction() {
   }
}