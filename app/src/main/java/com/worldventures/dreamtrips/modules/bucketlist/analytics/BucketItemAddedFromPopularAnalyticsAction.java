package com.worldventures.dreamtrips.modules.bucketlist.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist:popular bucket lists:search",
                trackers = AdobeTracker.TRACKER_KEY)
public class BucketItemAddedFromPopularAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("bucketlistadd") String query;

   public BucketItemAddedFromPopularAnalyticsAction(String query) {
      this.query = query;
   }
}
