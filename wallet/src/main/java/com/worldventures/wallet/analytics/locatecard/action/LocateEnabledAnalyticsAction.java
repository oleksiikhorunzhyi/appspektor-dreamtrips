package com.worldventures.wallet.analytics.locatecard.action;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "wallet:settings:locate smartcard:enable tracking",
                trackers = AdobeTracker.TRACKER_KEY)
public class LocateEnabledAnalyticsAction extends BaseLocateSmartCardAction {

   @Attribute("trackingenabled") String trackingEnabled = "Yes";
   @Attribute("enabletracking") String enableTracking = "1";
}
