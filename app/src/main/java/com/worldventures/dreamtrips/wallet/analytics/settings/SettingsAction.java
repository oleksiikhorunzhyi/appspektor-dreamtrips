package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SettingsAction extends WalletAnalyticsAction {
}
