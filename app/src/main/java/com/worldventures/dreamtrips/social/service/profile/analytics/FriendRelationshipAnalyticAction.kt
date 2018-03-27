package com.worldventures.dreamtrips.social.service.profile.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "friends_activity", trackers = [(AdobeTracker.TRACKER_KEY)])
class FriendRelationshipAnalyticAction private constructor(
      @Attribute("unfriend") var unfriendAction: String? = null,
      @Attribute("reject_friend_request") var rejectAction: String? = null,
      @Attribute("cancel_friend_request") var cancelAction: String? = null
) : BaseAnalyticsAction() {
   companion object {

      @JvmStatic
      fun cancelRequest() = FriendRelationshipAnalyticAction(cancelAction = "1")

      @JvmStatic
      fun rejectRequest() = FriendRelationshipAnalyticAction(rejectAction = "1")

      @JvmStatic
      fun unfriend() = FriendRelationshipAnalyticAction(unfriendAction = "1")
   }
}
