package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist", trackers = AdobeTracker.TRACKER_KEY)
public class AdobeStartUploadBucketPhotoAction extends BaseAnalyticsAction {

   @Attribute("upload_photo") final String uploadPhoto = "1";

   @Attribute("bucket_list_id") final String bucketListId;

   public AdobeStartUploadBucketPhotoAction(String bucketListId) {
      this.bucketListId = bucketListId;
   }
}
