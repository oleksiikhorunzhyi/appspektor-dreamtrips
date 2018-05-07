package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute

@AnalyticsEvent(action = "member_images", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class TripImageDeleteAnalyticsEvent(photoId: String) : TripImageAnalyticsEvent(photoId) {

   @field:Attribute("delete_image") internal val deleteImage = "1"
}
