package com.worldventures.wallet.analytics.locatecard.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:settings:locate smartcard:disable tracking",
                trackers = AdobeTracker.TRACKER_KEY)
public class LocateDisabledAnalyticsAction extends BaseLocateSmartCardAction {

   @Attribute("trackingenabled") String trackingEnabled = "No";
   @Attribute("disabletracking ") String disableTracking = "1";
}
