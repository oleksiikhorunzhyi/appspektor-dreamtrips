package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

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
