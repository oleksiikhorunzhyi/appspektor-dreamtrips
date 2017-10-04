package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "success_stories",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public abstract class ApptentiveSuccessStoryAction extends BaseAnalyticsAction {

   @AnalyticsEvent(action = "success_stories",
                   category = "nav_menu",
                   trackers = ApptentiveTracker.TRACKER_KEY)
   public static class ViewSuccessStoryAction extends ApptentiveSuccessStoryAction {
   }

   @AnalyticsEvent(action = "success_stories",
                   category = "nav_menu",
                   trackers = ApptentiveTracker.TRACKER_KEY)
   public static class LikeSuccessStoryAction extends ApptentiveSuccessStoryAction {
   }

   @AnalyticsEvent(action = "success_stories",
                   category = "nav_menu",
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
