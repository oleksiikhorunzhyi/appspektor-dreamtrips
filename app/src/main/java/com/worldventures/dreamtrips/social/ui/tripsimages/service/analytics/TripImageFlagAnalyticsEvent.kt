package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute

@AnalyticsEvent(action = "member_images", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class TripImageFlagAnalyticsEvent(photoId: String) : TripImageAnalyticsEvent(photoId) {

   @field:Attribute("flag_image") internal val flagImage = "1"
}
