package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Terms and Conditions",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class TermsAction extends WalletAnalyticsAction {
}
