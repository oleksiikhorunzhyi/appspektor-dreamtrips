package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:setup:Check Front of SmartCard",
                trackers = AdobeTracker.TRACKER_KEY)
public class CheckFrontAction extends WalletAnalyticsAction {
}
