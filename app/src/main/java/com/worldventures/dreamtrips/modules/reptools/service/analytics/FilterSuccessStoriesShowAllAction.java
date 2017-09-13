package com.worldventures.dreamtrips.modules.reptools.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:success_story", trackers = AdobeTracker.TRACKER_KEY)
public class FilterSuccessStoriesShowAllAction extends BaseAnalyticsAction {

   @Attribute("show_all")
   final String attribute = "1";
}
