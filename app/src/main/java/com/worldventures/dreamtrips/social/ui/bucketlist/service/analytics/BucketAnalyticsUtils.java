package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

public final class BucketAnalyticsUtils {

   private static final String ANALYTICS_DINING = "dining";
   private static final String ANALYTICS_ACTIVITIES = "activities";
   private static final String ANALYTICS_LOCATIONS = "locations";

   private BucketAnalyticsUtils() {
   }

   public static String getAnalyticsName(BucketItem.BucketType bucketType) {
      switch (bucketType) {
         case LOCATION:
            return ANALYTICS_LOCATIONS;
         case ACTIVITY:
            return ANALYTICS_ACTIVITIES;
         case DINING:
            return ANALYTICS_DINING;
         default:
            return "";
      }
   }
}
