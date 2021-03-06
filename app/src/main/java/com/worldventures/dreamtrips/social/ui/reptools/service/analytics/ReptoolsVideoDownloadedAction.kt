package com.worldventures.dreamtrips.social.ui.reptools.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "rep_tools:training_video", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ReptoolsVideoDownloadedAction(@field:Attribute("video_id")
                                    internal var videoName: String) : BaseAnalyticsAction() {

   @field:Attribute("download") internal val download = "1"
}
