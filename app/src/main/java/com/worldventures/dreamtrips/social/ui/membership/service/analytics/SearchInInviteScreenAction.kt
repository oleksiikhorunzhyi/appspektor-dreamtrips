package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "membership:rep_tools:invite_share", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class SearchInInviteScreenAction : BaseAnalyticsAction() {

   @field:Attribute("search") internal var search = "1"

}
