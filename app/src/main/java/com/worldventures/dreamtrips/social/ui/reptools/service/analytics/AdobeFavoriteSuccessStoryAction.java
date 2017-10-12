package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:success_story", trackers = AdobeTracker.TRACKER_KEY)
public class AdobeFavoriteSuccessStoryAction extends BaseAnalyticsAction {

   @Attribute("favorite")
   final String favorite = "1";

   @Attribute("story_id")
   final String storyId;

   public AdobeFavoriteSuccessStoryAction(String storyId) {
      this.storyId = storyId;
   }
}
