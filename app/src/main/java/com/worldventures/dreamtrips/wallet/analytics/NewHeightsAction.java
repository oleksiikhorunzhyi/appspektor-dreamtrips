package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@Deprecated
@AnalyticsEvent(action = "wallet:setup:Step 2: New Heights with flye",
                trackers = AdobeTracker.TRACKER_KEY)
public class NewHeightsAction extends WalletAnalyticsAction {
}
