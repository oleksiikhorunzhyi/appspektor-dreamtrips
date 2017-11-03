package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "membership-enroll-merchant", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class EnrollMerchantViewedAction(@field:Attribute("member_id") internal val memberId: String) : BaseAnalyticsAction() {

   @Attribute("view") internal val view = "1"
}
