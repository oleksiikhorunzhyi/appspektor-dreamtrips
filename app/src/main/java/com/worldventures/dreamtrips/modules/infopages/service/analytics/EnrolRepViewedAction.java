package com.worldventures.dreamtrips.modules.infopages.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:rep_enrollment", trackers = AdobeTracker.TRACKER_KEY)
public class EnrolRepViewedAction extends BaseAnalyticsAction {

   @Attribute("view")
   final String attribute = "1";
}
