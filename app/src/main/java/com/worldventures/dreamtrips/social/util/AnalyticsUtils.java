package com.worldventures.dreamtrips.social.util;

import com.worldventures.dreamtrips.social.ui.share.ShareType;

public class AnalyticsUtils {

   public static final String ATTRIBUTE_FACEBOOK = "facebook";
   public static final String ATTRIBUTE_TWITTER = "twitter";
   public static final String ATTRIBUTE_SHARING_UNRESOLVED = "unknown";

   public static String resolveSharingType(@ShareType String type) {
      switch (type) {
         case ShareType.FACEBOOK:
            return ATTRIBUTE_FACEBOOK;
         case ShareType.TWITTER:
            return ATTRIBUTE_TWITTER;
         default:
            return ATTRIBUTE_SHARING_UNRESOLVED;
      }
   }
}
