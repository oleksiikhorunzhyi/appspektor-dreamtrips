package com.worldventures.dreamtrips.social.ui.membership.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership:rep_tools:invite_share",
                trackers = AdobeTracker.TRACKER_KEY)
public class SearchInInviteScreenAction extends BaseAnalyticsAction {

   @Attribute("search") String search = "1";

}
