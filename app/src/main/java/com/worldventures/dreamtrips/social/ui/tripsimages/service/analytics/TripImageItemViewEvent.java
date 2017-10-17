package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "yshb_images", trackers = AdobeTracker.TRACKER_KEY)
public class TripImageItemViewEvent extends BaseAnalyticsAction {

   @Attribute("view") final String flagImage = "1";
   @Attribute("image_id") String imageId;

   public TripImageItemViewEvent(String imageId) {
      this.imageId = imageId;
   }
}
