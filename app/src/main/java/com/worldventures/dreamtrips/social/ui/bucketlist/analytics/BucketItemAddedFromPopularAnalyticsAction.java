package com.worldventures.dreamtrips.social.ui.bucketlist.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "bucketlist:popular bucket lists:search",
                trackers = AdobeTracker.TRACKER_KEY)
public class BucketItemAddedFromPopularAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("bucketlistadd") String query;

   public BucketItemAddedFromPopularAnalyticsAction(String query) {
      this.query = query;
   }
}
