package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 8:Syncing of Payment Cards",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SyncPaymentCardAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep8a")
   final String cardSetupStep8a = "1";
}