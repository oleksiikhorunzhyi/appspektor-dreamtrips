package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist", trackers = AdobeTracker.TRACKER_KEY)
public class AdobeStartUploadBucketPhotoAction extends BaseAnalyticsAction {

   @Attribute("upload_photo")
   final String uploadPhoto = "1";

   @Attribute("bucket_list_id")
   final String bucketListId;

   public AdobeStartUploadBucketPhotoAction(String bucketListId) {
      this.bucketListId = bucketListId;
   }
}
