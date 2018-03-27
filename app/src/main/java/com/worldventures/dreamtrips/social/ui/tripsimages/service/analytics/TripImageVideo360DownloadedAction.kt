package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "360_videos", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class TripImageVideo360DownloadedAction(@field:Attribute("video_id")
                                        internal var videoName: String) : BaseAnalyticsAction() {

   @field:Attribute("download")
   internal var download = "1"
}
