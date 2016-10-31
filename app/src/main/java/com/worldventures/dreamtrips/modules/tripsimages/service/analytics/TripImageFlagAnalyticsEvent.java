package com.worldventures.dreamtrips.modules.tripsimages.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageFlagAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("flag_image") final String flagImage = "1";

   public TripImageFlagAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
