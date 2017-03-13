package com.worldventures.dreamtrips.modules.auth.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "login",
                trackers = {AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY})
public class LoginAction extends BaseAnalyticsAction {

   @Attribute("login") String login;
   @Attribute("member_id") final String username;
   @Attribute("loggedin") String loggedIn;

   public LoginAction(String username, boolean userAlreadyLoggedIn) {
      this.username = username;
      if (userAlreadyLoggedIn) loggedIn = "1";
      else login = "1";
   }
}
