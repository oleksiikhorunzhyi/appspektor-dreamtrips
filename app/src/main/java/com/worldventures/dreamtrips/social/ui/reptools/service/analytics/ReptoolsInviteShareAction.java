package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:invite_share", trackers = AdobeTracker.TRACKER_KEY)
public class ReptoolsInviteShareAction extends BaseAnalyticsAction {

   @Attribute("view") final String view = "1";
}
