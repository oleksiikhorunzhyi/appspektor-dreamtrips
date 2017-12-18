package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageLikedAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("like") final String like = "1";

   public TripImageLikedAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
