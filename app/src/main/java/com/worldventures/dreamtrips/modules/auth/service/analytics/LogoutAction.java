package com.worldventures.dreamtrips.modules.auth.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Logout",
                trackers = {AdobeTracker.TRACKER_KEY})
public class LogoutAction extends BaseAnalyticsAction {

   @Attribute("logout") final String logout = "1";


}
