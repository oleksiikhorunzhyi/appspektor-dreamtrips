package com.worldventures.wallet.analytics.wizard;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 6:Create your PIN",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SetPinAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep6") final String cardSetupStep6 = "1";
}
