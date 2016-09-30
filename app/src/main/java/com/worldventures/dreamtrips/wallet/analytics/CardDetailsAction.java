package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Card Detail",
                trackers = AdobeTracker.TRACKER_KEY)
public class CardDetailsAction extends BaseCardDetailsWithDefaultAction {
}
