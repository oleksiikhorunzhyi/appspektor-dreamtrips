package com.worldventures.dreamtrips.modules.feed.model.util;

import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;

public class FeedEntityUtil {
   private FeedEntityUtil() {
   }

   public static String getFeedEntityTrackingType(FeedEntityHolder.Type type) {
      final String trackingType;
      switch (type) {
         case BUCKET_LIST_ITEM:
            trackingType = "bucket_list_id";
            break;
         case POST:
            trackingType = "post_id";
            break;
         case TRIP:
            trackingType = "trip_id";
            break;
         case PHOTO:
            trackingType = "photo_id";
            break;
         default:
            trackingType = "";
            break;
      }
      return trackingType;
   }
}
