package com.worldventures.dreamtrips.modules.infopages.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.ActionPart;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

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
