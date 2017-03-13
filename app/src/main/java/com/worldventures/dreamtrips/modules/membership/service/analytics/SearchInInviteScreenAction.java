package com.worldventures.dreamtrips.modules.membership.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "membership:rep_tools:invite_share",
                trackers = AdobeTracker.TRACKER_KEY)
public class SearchInInviteScreenAction extends BaseAnalyticsAction {

   @Attribute("search") String search = "1";

}
