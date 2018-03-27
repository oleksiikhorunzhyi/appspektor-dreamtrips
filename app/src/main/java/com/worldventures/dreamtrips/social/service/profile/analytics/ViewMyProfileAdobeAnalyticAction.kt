package com.worldventures.dreamtrips.social.service.profile.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "profile", trackers = [(AdobeTracker.TRACKER_KEY)])
class ViewMyProfileAdobeAnalyticAction(@Attribute("view") val view: String = "1") : BaseAnalyticsAction()
