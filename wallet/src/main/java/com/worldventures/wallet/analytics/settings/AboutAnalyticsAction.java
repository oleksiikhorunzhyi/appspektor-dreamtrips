package com.worldventures.wallet.analytics.settings;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:about",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class AboutAnalyticsAction extends WalletAnalyticsAction {
}
