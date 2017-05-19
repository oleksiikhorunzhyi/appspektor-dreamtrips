package com.worldventures.dreamtrips.wallet.analytics.locatecard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:settings:locate smartcard:enable tracking",
                trackers = AdobeTracker.TRACKER_KEY)
public class LocateEnabledAnalyticsAction extends BaseLocateSmartCardAction {

   @Attribute("trackingenabled") String trackingEnabled = "Yes";
   @Attribute("enabletracking") String enableTracking = "1";
}
