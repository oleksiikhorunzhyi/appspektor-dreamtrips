package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

public class TripImageAnalyticsEvent extends BaseAnalyticsAction {

   @Attribute("photo_id") final String photoId;

   public TripImageAnalyticsEvent(String photoId) {
      this.photoId = photoId;
   }
}
