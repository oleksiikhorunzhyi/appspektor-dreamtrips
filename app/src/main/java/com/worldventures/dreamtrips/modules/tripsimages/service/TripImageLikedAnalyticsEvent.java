package com.worldventures.dreamtrips.modules.tripsimages.service;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "member_images",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImageLikedAnalyticsEvent extends TripImageAnalyticsEvent {

   @Attribute("like") final String like = "1";

   public TripImageLikedAnalyticsEvent(String photoId) {
      super(photoId);
   }
}
