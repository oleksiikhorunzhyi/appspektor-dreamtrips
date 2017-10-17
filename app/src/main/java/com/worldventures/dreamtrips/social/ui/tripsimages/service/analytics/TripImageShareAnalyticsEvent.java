package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageShareAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("share_image") final String shareImage = "1";

   public TripImageShareAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
