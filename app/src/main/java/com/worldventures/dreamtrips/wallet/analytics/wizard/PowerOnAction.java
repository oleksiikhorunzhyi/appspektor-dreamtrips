package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:Power on your flye SmartCard",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class PowerOnAction extends WalletAnalyticsAction {
}