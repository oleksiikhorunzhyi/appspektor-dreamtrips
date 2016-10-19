package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:Welcome to flye SmartCard setup",
                trackers = AdobeTracker.TRACKER_KEY)
public class WelcomeAction extends WalletAnalyticsAction {
}
