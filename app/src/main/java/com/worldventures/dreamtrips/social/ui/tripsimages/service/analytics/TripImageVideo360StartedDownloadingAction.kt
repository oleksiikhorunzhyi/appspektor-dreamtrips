package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.ApptentiveTracker
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "membership", category = "nav_menu", trackers = arrayOf(ApptentiveTracker.TRACKER_KEY))
class TripImageVideo360StartedDownloadingAction : BaseAnalyticsAction()
