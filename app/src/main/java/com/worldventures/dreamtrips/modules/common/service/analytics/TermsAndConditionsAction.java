package com.worldventures.dreamtrips.modules.common.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Terms and Conditions", trackers = AdobeTracker.TRACKER_KEY)
public class TermsAndConditionsAction extends BaseAnalyticsAction {

   @Attribute("optinoptout")
   final String attr;

   public TermsAndConditionsAction(boolean accepted) {
      attr = accepted ? "Opt In" : "Opt Out";
   }
}
