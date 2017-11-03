package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "membership:rep_tools:invite_share", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SearchInInviteScreenAction : BaseAnalyticsAction() {

   @Attribute("search") internal var search = "1"

}
