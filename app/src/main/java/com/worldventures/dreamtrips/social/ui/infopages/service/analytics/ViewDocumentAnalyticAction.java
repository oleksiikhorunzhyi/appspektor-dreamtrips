package com.worldventures.dreamtrips.social.ui.infopages.service.analytics;

import com.worldventures.core.service.analytics.ActionPart;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "${action}",
                category = "nav_menu",
                trackers = {AdobeTracker.TRACKER_KEY, ApptentiveTracker.TRACKER_KEY})
public class ViewDocumentAnalyticAction extends BaseAnalyticsAction {

   @Attribute("view") final String view = "1";
   @ActionPart final String action;
   @Attribute("member_id") final String memberId;

   public ViewDocumentAnalyticAction(String action, String memberId) {
      this.action = action;
      this.memberId = memberId;
   }
}
