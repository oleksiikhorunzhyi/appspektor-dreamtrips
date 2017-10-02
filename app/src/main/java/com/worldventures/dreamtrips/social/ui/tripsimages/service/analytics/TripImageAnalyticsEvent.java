package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

public class TripImageAnalyticsEvent extends BaseAnalyticsAction {

   @Attribute("photo_id") final String photoId;

   public TripImageAnalyticsEvent(String photoId) {
      this.photoId = photoId;
   }
}
