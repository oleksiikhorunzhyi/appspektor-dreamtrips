package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.ApptentiveTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "membership:rep_tools:invite_share", trackers = arrayOf(AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY))
class ViewInviteScreenAction : BaseAnalyticsAction() {

   @field:Attribute("view") internal var view = "1"

}
