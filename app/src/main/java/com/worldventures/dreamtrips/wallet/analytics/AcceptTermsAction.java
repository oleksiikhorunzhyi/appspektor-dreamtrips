package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "wallet:setup:Accept Terms and Conditions",
                trackers = AdobeTracker.TRACKER_KEY)
public class AcceptTermsAction extends WalletAnalyticsAction {
}
