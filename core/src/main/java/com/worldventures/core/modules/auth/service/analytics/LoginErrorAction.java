package com.worldventures.core.modules.auth.service.analytics;


import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "login",
                trackers = AdobeTracker.TRACKER_KEY)
public class LoginErrorAction extends BaseAnalyticsAction {

   @Attribute("login_error") final String loginError = "1";
}
