package com.worldventures.dreamtrips.modules.feed.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "photo_upload",
                trackers = AdobeTracker.TRACKER_KEY)
public class PhotoUploadedAction extends BaseAnalyticsAction {
   @Attribute("uploadamt") final String uploadCount;

   public PhotoUploadedAction(int uploadCount) {
      this.uploadCount = String.valueOf(uploadCount);
   }
}
