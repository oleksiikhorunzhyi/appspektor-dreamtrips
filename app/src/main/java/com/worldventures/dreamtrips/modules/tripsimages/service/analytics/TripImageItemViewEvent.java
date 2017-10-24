package com.worldventures.dreamtrips.modules.tripsimages.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "yshb_images", trackers = AdobeTracker.TRACKER_KEY)
public class TripImageItemViewEvent extends BaseAnalyticsAction {

   @Attribute("view") final String flagImage = "1";
   @Attribute("image_id") String imageId;

   public TripImageItemViewEvent(String imageId) {
      this.imageId = imageId;
   }
}
