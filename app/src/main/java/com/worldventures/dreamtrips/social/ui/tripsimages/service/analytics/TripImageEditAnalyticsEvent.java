package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageEditAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("edit_image") final String editImage = "1";

   public TripImageEditAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
