package com.worldventures.dreamtrips.social.service.profile.analytics

import com.worldventures.core.service.analytics.ApptentiveTracker
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "profile", category = "nav_menu", trackers = [(ApptentiveTracker.TRACKER_KEY)])
class ProfileUploadingAnalyticAction : BaseAnalyticsAction()
