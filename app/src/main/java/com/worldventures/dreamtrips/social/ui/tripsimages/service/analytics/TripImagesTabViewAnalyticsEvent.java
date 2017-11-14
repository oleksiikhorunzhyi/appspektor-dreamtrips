package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics;

import com.worldventures.core.service.analytics.ActionPart;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

@AnalyticsEvent(action = "trip_images:${tabName}",
                trackers = AdobeTracker.TRACKER_KEY)
public class TripImagesTabViewAnalyticsEvent extends BaseAnalyticsAction {

   @ActionPart String tabName;

   public TripImagesTabViewAnalyticsEvent(String tabName) {
      this.tabName = tabName;
   }

   public static TripImagesTabViewAnalyticsEvent forTripImages(TripImagesArgs tripImagesArgs) {
      /*
      switch (tripImagesType) {
         case MEMBERS_IMAGES:
            return new TripImagesTabViewAnalyticsEvent("member_images");
         case YOU_SHOULD_BE_HERE:
            return new TripImagesTabViewAnalyticsEvent("ysbh_images");
         case INSPIRE_ME:
            return new TripImagesTabViewAnalyticsEvent("inspire_me_images");
         default:
            return new TripImagesTabViewAnalyticsEvent("my_images");
      }
      */
      return new TripImagesTabViewAnalyticsEvent("member_images");
   }

   public static TripImagesTabViewAnalyticsEvent for360Video() {
      return new TripImagesTabViewAnalyticsEvent("videos_360");
   }

}
