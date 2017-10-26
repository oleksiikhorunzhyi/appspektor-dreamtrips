package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:success_story", trackers = AdobeTracker.TRACKER_KEY)
public class AdobeFavoriteSuccessStoryAction extends BaseAnalyticsAction {

   @Attribute("favorite") final String favorite = "1";

   @Attribute("story_id") final String storyId;

   public AdobeFavoriteSuccessStoryAction(String storyId) {
      this.storyId = storyId;
   }
}
