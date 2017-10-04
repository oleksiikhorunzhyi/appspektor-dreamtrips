package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:invite_share", trackers = AdobeTracker.TRACKER_KEY)
public class ReptoolsInviteShareAction extends BaseAnalyticsAction {

   @Attribute("view")
   final String view = "1";
}
