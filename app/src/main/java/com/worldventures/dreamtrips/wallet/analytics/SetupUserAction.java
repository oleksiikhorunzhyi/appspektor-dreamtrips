package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:setup:Step 4:Set Display Photo and Name",
                trackers = AdobeTracker.TRACKER_KEY)
public class SetupUserAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep4") final String cardSetupStep4 = "1";
}
