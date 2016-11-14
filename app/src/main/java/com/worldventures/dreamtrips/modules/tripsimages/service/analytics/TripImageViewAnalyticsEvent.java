package com.worldventures.dreamtrips.modules.tripsimages.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageViewAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("view") final String view = "1";

   public TripImageViewAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
