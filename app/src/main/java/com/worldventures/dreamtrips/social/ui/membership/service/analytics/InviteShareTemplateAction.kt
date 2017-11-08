package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.ApptentiveTracker
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "rep_tools:invite_share", category = "nav_menu", trackers = arrayOf(ApptentiveTracker.TRACKER_KEY))
class InviteShareTemplateAction : BaseAnalyticsAction()
