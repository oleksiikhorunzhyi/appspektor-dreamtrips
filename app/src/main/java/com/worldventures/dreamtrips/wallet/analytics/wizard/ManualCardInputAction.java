package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 1:Manual Input of SCID",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class ManualCardInputAction extends WalletAnalyticsAction {
}
