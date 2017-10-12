package com.worldventures.dreamtrips.modules.trips.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "member_images", trackers = AdobeTracker.TRACKER_KEY)
public class UploadTripImageAnalyticAction extends BaseAnalyticsAction {

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
