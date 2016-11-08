package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:setup:Terms and Conditions",
                trackers = AdobeTracker.TRACKER_KEY)
public class TermsAction extends WalletAnalyticsAction {
}
