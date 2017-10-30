package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "member_images", trackers = AdobeTracker.TRACKER_KEY)
public final class UploadTripImageAnalyticAction extends BaseAnalyticsAction {

   @Attribute("my_images") String myImages;
   @Attribute("member_images") String memberImages;

   private UploadTripImageAnalyticAction() {
   }

   public static UploadTripImageAnalyticAction fromMyImages() {
      UploadTripImageAnalyticAction action = new UploadTripImageAnalyticAction();
      action.myImages = "1";
      return action;
   }

   public static UploadTripImageAnalyticAction fromMemberImages() {
      UploadTripImageAnalyticAction action = new UploadTripImageAnalyticAction();
      action.memberImages = "1";
      return action;
   }

}
