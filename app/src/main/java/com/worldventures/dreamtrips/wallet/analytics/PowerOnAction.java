package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Power on your flye SmartCard",
                trackers = AdobeTracker.TRACKER_KEY)
public class PowerOnAction extends WalletAnalyticsAction {
}
