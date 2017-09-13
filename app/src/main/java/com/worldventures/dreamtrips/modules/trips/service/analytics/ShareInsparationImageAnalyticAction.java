package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.common.model.ShareType;

@AnalyticsEvent(action = "inspireme_share", trackers = AdobeTracker.TRACKER_KEY)
public class ShareInsparationImageAnalyticAction extends BaseAnalyticsAction {

   private static final String ATTRIBUTE_FACEBOOK = "facebook";
   private static final String ATTRIBUTE_TWITTER = "twitter";
   private static final String ATTRIBUTE_SHARING_UNRESOLVED = "unknown";

   @Attribute("id") String id;
   @Attribute("type") String type;

   public ShareInsparationImageAnalyticAction(String id, @ShareType String type) {
      this.id = id;
      this.type = resolveSharingType(type);
   }

   private String resolveSharingType(@ShareType String type) {
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
