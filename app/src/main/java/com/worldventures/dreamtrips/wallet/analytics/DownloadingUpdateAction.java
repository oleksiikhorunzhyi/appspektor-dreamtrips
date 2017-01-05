package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "dta:wallet:SmartCard Update:Step 2",
                trackers = AdobeTracker.TRACKER_KEY)
public class DownloadingUpdateAction extends WalletAnalyticsAction {

   @Attribute("scupdatestep2") final String updateStep2 = "1";

}
