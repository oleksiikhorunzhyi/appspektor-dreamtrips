package com.worldventures.dreamtrips.modules.tripsimages.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageShareAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("share_image") final String shareImage = "1";

   public TripImageShareAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
