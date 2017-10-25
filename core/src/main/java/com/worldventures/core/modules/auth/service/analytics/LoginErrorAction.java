package com.worldventures.core.modules.auth.service.analytics;


import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "login",
                trackers = AdobeTracker.TRACKER_KEY)
public class LoginErrorAction extends BaseAnalyticsAction {

   @Attribute("login_error") final String loginError = "1";
}
