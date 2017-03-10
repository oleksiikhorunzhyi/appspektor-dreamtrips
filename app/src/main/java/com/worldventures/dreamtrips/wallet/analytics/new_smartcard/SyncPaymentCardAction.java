package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 8:Syncing of Payment Cards",
                trackers = AdobeTracker.TRACKER_KEY)
public class SyncPaymentCardAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep8a")
   final String cardSetupStep8a = "1";
}