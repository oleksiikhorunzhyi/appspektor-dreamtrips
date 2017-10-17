package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 9:Setup of New Card Complete",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class NewCardSetupCompleteAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep9") final String cardSetupStep9 = "1";
}
