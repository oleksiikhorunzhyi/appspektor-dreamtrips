package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Support for your device is coming soon",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SupportDeviceAction extends WalletAnalyticsAction {
}
