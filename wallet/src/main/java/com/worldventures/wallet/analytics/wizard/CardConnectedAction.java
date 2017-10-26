package com.worldventures.wallet.analytics.wizard;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 3:Card Successfully Connected",
                trackers = AdobeTracker.TRACKER_KEY)
public class CardConnectedAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep3") final String cardSetupStep3 = "1";
}
