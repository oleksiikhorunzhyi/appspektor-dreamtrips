package com.worldventures.dreamtrips.modules.auth.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "login",
                trackers = AdobeTracker.TRACKER_KEY)
public class LoginErrorAction extends BaseAnalyticsAction {

   @Attribute("login_error") final String loginError = "1";
}
