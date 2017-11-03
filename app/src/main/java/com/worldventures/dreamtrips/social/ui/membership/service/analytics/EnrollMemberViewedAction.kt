package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.ApptentiveTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "membership:enroll-member", category = "nav_menu", trackers = arrayOf(AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY))
class EnrollMemberViewedAction(@field:Attribute("member_id") internal val memberId: String) : BaseAnalyticsAction() {

   @Attribute("view") internal val view = "1"
}
