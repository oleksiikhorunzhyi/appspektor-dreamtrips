package com.worldventures.dreamtrips.modules.reptools.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "success_stories",
                category = "nav_menu",
                trackers = ApptentiveTracker.TRACKER_KEY)
public class SuccessStoriesViewedAction extends BaseAnalyticsAction {
}
