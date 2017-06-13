package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Support for your device is coming soon",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SupportDeviceAction extends WalletAnalyticsAction {
}
