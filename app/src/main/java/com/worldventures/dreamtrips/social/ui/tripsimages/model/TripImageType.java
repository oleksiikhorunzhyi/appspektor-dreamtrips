package com.worldventures.dreamtrips.social.ui.tripsimages.model;

import com.worldventures.dreamtrips.api.entity.model.EntityHolder;

public enum TripImageType {
   PHOTO, VIDEO, UNKNOWN;

   public static TripImageType from(EntityHolder.Type type) {
      switch (type) {
         case PHOTO:
            return TripImageType.PHOTO;
         case VIDEO:
            return TripImageType.VIDEO;
         default:
            return TripImageType.UNKNOWN;
      }
   }
}
