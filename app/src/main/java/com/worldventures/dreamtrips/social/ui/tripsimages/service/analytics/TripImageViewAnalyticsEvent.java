package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageViewAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("view") final String view = "1";

   public TripImageViewAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
