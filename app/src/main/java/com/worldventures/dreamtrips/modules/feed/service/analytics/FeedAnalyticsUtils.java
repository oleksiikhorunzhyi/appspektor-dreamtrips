package com.worldventures.dreamtrips.modules.feed.service.analytics;

import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;

public final class FeedAnalyticsUtils {

   private FeedAnalyticsUtils() {
   }

   public static String getIdAttributeName(FeedEntityHolder.Type type) {
      switch (type) {
         case BUCKET_LIST_ITEM:
            return "bucket_list_id";
         case POST:
            return "post_id";
         case TRIP:
            return "trip_id";
         case PHOTO:
            return "photo_id";
         default:
            return "";
      }
   }
}
