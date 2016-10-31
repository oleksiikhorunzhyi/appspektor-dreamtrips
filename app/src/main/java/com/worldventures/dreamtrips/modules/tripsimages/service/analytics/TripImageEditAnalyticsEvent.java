package com.worldventures.dreamtrips.modules.tripsimages.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageEditAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("edit_image") final String editImage = "1";

   public TripImageEditAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
