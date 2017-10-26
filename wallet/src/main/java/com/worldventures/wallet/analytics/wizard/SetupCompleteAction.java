package com.worldventures.wallet.analytics.wizard;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 8:Setup Complete",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SetupCompleteAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep8") final String cardSetupStep8 = "1";
}
