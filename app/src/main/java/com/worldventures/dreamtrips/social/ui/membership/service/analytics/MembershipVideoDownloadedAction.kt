package com.worldventures.dreamtrips.social.ui.membership.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "membership", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class MembershipVideoDownloadedAction(@field:Attribute("video_id")
                                      internal var videoName: String) : BaseAnalyticsAction() {

   @field:Attribute("download") internal val download = "1"
}
