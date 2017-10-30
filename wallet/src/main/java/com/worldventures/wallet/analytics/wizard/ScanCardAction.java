package com.worldventures.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 1:Scan Card",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class ScanCardAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstart") final String cardSetupStart = "1";
}
