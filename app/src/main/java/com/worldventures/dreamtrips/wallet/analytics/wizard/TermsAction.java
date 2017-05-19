package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Terms and Conditions",
                trackers = AdobeTracker.TRACKER_KEY)
public class TermsAction extends WalletAnalyticsAction {
}
