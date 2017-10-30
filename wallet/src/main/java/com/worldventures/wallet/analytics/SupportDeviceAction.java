package com.worldventures.wallet.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Support for your device is coming soon",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SupportDeviceAction extends WalletAnalyticsAction {
}
