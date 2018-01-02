package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.core.service.analytics.ActionPart
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "trip_images:\${tabName}", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class TripImagesTabViewAnalyticsEvent(@field:ActionPart internal var tabName: String) : BaseAnalyticsAction() {
   companion object {

      fun for360Video(): TripImagesTabViewAnalyticsEvent {
         return TripImagesTabViewAnalyticsEvent("videos_360")
      }
   }

}
