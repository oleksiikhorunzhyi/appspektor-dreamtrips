package com.worldventures.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SettingsGeneralAction extends WalletAnalyticsAction {
}
