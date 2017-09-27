package com.worldventures.dreamtrips.social.ui.profile.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "profile",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewMyProfileAdobeAnalyticAction extends BaseAnalyticsAction {

   @Attribute("view") final String view = "1";

}
