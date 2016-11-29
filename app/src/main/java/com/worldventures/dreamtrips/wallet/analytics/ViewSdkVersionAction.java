package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "dta:wallet:SmartCard Update:SmartCard Up to Date",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewSdkVersionAction extends WalletAnalyticsAction {
}