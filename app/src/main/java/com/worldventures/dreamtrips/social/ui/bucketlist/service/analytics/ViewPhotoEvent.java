package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist", trackers = AdobeTracker.TRACKER_KEY)
public class ViewPhotoEvent extends BaseAnalyticsAction {

   @Attribute("view_photo")
   String viewPhoto = "1";

   @Attribute("bucket_list_id")
   String bucketId;

   public ViewPhotoEvent(String bucketId) {
      this.bucketId = bucketId;
   }
}
