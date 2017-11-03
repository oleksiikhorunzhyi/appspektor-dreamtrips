package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.ApptentiveTracker
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "invite_share_select_contacts", category = "nav_menu", trackers = arrayOf(ApptentiveTracker.TRACKER_KEY))
class InviteShareContactsAction : BaseAnalyticsAction()
