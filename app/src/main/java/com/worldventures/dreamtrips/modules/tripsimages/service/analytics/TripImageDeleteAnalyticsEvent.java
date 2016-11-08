package com.worldventures.dreamtrips.modules.tripsimages.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageDeleteAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("delete_image") final String deleteImage = "1";

   public TripImageDeleteAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
