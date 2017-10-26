package com.worldventures.core.modules.auth.service.analytics;


import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;

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
