package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 7:Your PIN is set",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class PinWasSetAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep7") final String cardsetupstep7 = "1";
}