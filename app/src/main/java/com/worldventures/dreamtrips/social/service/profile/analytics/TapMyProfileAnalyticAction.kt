package com.worldventures.dreamtrips.social.service.profile.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "profile", trackers = [(AdobeTracker.TRACKER_KEY)])
class TapMyProfileAnalyticAction(
      @Attribute("show_bucketlist") var bucketListAttribute: String? = null,
      @Attribute("show_trips") var tripsAttribute: String? = null,
      @Attribute("show_friends") var friendsAttribute: String? = null,
      @Attribute("new_post") var postAttribute: String? = null
) : BaseAnalyticsAction() {

   companion object {

      @JvmStatic
      fun tapOnBucketList() = TapMyProfileAnalyticAction(bucketListAttribute = "1")

      @JvmStatic
      fun tapOnTrips() = TapMyProfileAnalyticAction(tripsAttribute = "1")

      @JvmStatic
      fun tapOnFriends() = TapMyProfileAnalyticAction(friendsAttribute = "1")

      @JvmStatic
      fun tapOnPost() = TapMyProfileAnalyticAction(postAttribute = "1")
   }
}
