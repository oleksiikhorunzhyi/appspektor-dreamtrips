package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;

@AnalyticsEvent(action = "dta:wallet:SmartCard Update:Ready Checklist:Install Later",
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateInstallLaterAction extends WalletAnalyticsAction {
}
