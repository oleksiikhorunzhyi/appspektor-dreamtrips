package com.worldventures.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 1:Manual Input of SCID",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class ManualCardInputAction extends WalletAnalyticsAction {
}
