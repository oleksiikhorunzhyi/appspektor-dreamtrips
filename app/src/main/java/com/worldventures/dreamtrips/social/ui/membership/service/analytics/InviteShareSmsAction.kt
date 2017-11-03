package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.ApptentiveTracker
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "invite_share_send_sms", trackers = arrayOf(ApptentiveTracker.TRACKER_KEY))
class InviteShareSmsAction : BaseAnalyticsAction()
