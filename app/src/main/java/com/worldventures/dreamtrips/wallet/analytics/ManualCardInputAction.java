package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:setup:Step 1:Manual Input of SCID",
                trackers = AdobeTracker.TRACKER_KEY)
public class ManualCardInputAction extends WalletAnalyticsAction {
}
