package com.worldventures.wallet.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;

@AnalyticsEvent(action = "wallet:Support for your device is coming soon",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SupportDeviceAction extends WalletAnalyticsAction {
}
