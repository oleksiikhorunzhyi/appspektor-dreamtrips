package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:smartcard profile",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SmartCardProfileAction extends WalletAnalyticsAction {
}
