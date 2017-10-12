package com.worldventures.dreamtrips.social.ui.profile.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "profile",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewMyProfileAdobeAnalyticAction extends BaseAnalyticsAction {

   @Attribute("view") final String view = "1";

}
