package com.worldventures.dreamtrips.wallet.analytics.locatecard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:settings:locate smartcard:disable tracking",
                trackers = AdobeTracker.TRACKER_KEY)
public class LocateDisabledAnalyticsAction extends BaseLocateSmartCardAction {

   @Attribute("trackingenabled") String trackingEnabled = "No";
   @Attribute("disabletracking ") String disableTracking = "1";
}
