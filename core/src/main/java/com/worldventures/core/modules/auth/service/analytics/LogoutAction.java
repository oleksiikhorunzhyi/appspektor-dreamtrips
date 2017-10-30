package com.worldventures.core.modules.auth.service.analytics;


import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Logout",
                trackers = {AdobeTracker.TRACKER_KEY})
public class LogoutAction extends BaseAnalyticsAction {

   @Attribute("logout") final String logout = "1";


}
