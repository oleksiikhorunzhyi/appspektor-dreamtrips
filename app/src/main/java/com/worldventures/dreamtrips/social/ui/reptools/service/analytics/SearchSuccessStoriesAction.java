package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:success_story", trackers = AdobeTracker.TRACKER_KEY)
public class SearchSuccessStoriesAction extends BaseAnalyticsAction {

   @Attribute("search") final String attribute = "1";
}
