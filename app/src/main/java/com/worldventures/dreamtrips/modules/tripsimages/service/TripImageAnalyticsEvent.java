package com.worldventures.dreamtrips.modules.tripsimages.service;


import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

public class TripImageAnalyticsEvent extends BaseAnalyticsAction {

   @Attribute("photo_id") final String photoId;

   public TripImageAnalyticsEvent(String photoId) {
      this.photoId = photoId;
   }
}
