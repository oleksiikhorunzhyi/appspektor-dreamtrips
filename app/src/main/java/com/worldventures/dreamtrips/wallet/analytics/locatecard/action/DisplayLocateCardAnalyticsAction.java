package com.worldventures.dreamtrips.wallet.analytics.locatecard.action;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;

@AnalyticsEvent(action = "wallet:settings:locate smartcard",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class DisplayLocateCardAnalyticsAction extends BaseLocateSmartCardAction {

   @Attribute("trackingenabled") final String trackingEnabled = "No";
}
