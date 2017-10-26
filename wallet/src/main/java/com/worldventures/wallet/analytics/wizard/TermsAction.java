package com.worldventures.wallet.analytics.wizard;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Terms and Conditions",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class TermsAction extends WalletAnalyticsAction {
}
