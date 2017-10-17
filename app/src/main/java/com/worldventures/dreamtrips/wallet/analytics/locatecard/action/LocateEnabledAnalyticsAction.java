package com.worldventures.dreamtrips.wallet.analytics.locatecard.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "wallet:settings:locate smartcard:enable tracking",
                trackers = AdobeTracker.TRACKER_KEY)
public class LocateEnabledAnalyticsAction extends BaseLocateSmartCardAction {

   @Attribute("trackingenabled") String trackingEnabled = "Yes";
   @Attribute("enabletracking") String enableTracking = "1";
}
