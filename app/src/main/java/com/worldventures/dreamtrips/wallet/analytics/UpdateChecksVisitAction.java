package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "dta:wallet:SmartCard Update:Ready Checklist",
                trackers = AdobeTracker.TRACKER_KEY)
public class UpdateChecksVisitAction extends WalletAnalyticsAction {

   @Attribute("scupdatestep2") final String udateStep2 = "1";

}
