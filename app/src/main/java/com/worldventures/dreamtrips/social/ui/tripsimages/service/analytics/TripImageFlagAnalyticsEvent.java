package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageFlagAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("flag_image") final String flagImage = "1";

   public TripImageFlagAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
