package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Check Front of SmartCard",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class CheckFrontAction extends WalletAnalyticsAction {
}
