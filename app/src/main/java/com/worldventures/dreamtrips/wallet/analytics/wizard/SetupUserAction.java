package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 4:Set Display Photo and Name",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SetupUserAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep4") final String cardSetupStep4 = "1";
}
