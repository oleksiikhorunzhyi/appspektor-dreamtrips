package com.worldventures.dreamtrips.social.ui.reptools.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "training_videos", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class AdobeTrainingVideosViewedAction : BaseAnalyticsAction() {

   @field:Attribute("list") internal var attribute = "1"
}
