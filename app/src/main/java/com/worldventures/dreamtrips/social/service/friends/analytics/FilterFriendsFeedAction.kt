package com.worldventures.dreamtrips.social.service.friends.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AttributeMap
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "friends_activity", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class FilterFriendsFeedAction(circleType: String) : BaseAnalyticsAction() {
   @field:AttributeMap
   val map: Map<String, String> = mapOf(Pair("friends_filter_" + circleType, "1"))
}
