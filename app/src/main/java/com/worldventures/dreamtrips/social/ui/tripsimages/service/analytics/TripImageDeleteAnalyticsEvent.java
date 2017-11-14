package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageDeleteAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("delete_image") final String deleteImage = "1";

   public TripImageDeleteAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
