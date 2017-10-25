package com.worldventures.dreamtrips.social.ui.infopages.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "rep_tools:rep_enrollment", trackers = AdobeTracker.TRACKER_KEY)
public class EnrolRepViewedAction extends BaseAnalyticsAction {

   @Attribute("view") final String attribute = "1";
}
