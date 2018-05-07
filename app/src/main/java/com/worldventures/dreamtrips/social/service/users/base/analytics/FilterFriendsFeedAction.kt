package com.worldventures.dreamtrips.social.service.users.base.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.AttributeMap
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "friends_activity", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class FilterFriendsFeedAction(circleType: String) : BaseAnalyticsAction() {
   @AttributeMap
   val map: Map<String, String> = mapOf(Pair("friends_filter_" + circleType, "1"))
}
