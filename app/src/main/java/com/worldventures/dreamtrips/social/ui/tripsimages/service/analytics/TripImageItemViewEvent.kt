package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction

@AnalyticsEvent(action = "yshb_images", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class TripImageItemViewEvent(@field:Attribute("image_id") internal var imageId: String) : BaseAnalyticsAction() {

   @field:Attribute("view") internal val flagImage = "1"
}
