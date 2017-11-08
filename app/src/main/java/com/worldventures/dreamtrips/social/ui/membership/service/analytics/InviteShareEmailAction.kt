package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.ApptentiveTracker
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "invite_share_send_email", trackers = arrayOf(ApptentiveTracker.TRACKER_KEY))
class InviteShareEmailAction : BaseAnalyticsAction()
