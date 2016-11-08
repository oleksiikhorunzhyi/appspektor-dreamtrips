package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:setup:Step 2:Set Display Photo and Name",
                trackers = AdobeTracker.TRACKER_KEY)
public class SetupUserAction extends WalletAnalyticsAction {
}
