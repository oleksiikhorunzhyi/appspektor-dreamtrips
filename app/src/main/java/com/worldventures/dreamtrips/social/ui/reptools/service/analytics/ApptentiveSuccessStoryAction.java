package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

import static com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveSuccessStoryAction.ACTION;
import static com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ApptentiveSuccessStoryAction.CATEGORY;

@AnalyticsEvent(action = ACTION,
                category = CATEGORY,
                trackers = ApptentiveTracker.TRACKER_KEY)
public abstract class ApptentiveSuccessStoryAction extends BaseAnalyticsAction {

   final static String ACTION = "success_stories";
   final static String CATEGORY = "nav_menu";

   @AnalyticsEvent(action = ACTION,
                   category = CATEGORY,
                   trackers = ApptentiveTracker.TRACKER_KEY)
   public static class ViewSuccessStoryAction extends ApptentiveSuccessStoryAction {
   }

   @AnalyticsEvent(action = ACTION,
                   category = CATEGORY,
                   trackers = ApptentiveTracker.TRACKER_KEY)
   public static class LikeSuccessStoryAction extends ApptentiveSuccessStoryAction {
   }

   @AnalyticsEvent(action = ACTION,
                   category = CATEGORY,
                   trackers = ApptentiveTracker.TRACKER_KEY)
   public static class UnlikeSuccessStoryAction extends ApptentiveSuccessStoryAction {
   }

   public static ApptentiveSuccessStoryAction view() {
      return new ViewSuccessStoryAction();
   }

   public static ApptentiveSuccessStoryAction like() {
      return new LikeSuccessStoryAction();
   }

   public static ApptentiveSuccessStoryAction unlike() {
      return new UnlikeSuccessStoryAction();
   }
}
