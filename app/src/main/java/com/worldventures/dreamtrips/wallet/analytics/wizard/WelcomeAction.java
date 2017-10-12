package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:Welcome to flye SmartCard setup",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class WelcomeAction extends WalletAnalyticsAction {
}
