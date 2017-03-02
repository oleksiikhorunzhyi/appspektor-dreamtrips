package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 8:Setup Complete",
                trackers = AdobeTracker.TRACKER_KEY)
public class SetupCompleteAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep8") final String cardSetupStep8 = "1";
}
