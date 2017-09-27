package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:success_story", trackers = AdobeTracker.TRACKER_KEY)
public class SearchSuccessStoriesAction extends BaseAnalyticsAction {

   @Attribute("search")
   final String attribute = "1";
}
