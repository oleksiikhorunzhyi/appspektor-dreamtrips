package com.worldventures.dreamtrips.modules.common.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Terms and Conditions", trackers = AdobeTracker.TRACKER_KEY)
public class TermsAndConditionsAction extends BaseAnalyticsAction {

   @Attribute("optinoptout") final String attr;

   public TermsAndConditionsAction(boolean accepted) {
      attr = accepted ? "Opt In" : "Opt Out";
   }
}
