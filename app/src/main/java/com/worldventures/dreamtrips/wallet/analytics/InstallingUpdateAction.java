package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "dta:wallet:SmartCard Update:Step 4:Installing Update",
                trackers = AdobeTracker.TRACKER_KEY)
public class InstallingUpdateAction extends WalletAnalyticsAction {

   @Attribute("scupdatestep4") final String updateStep4 = "1";

}
