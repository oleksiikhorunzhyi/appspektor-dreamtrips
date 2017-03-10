package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 9:Setup of New Card Complete",
                trackers = AdobeTracker.TRACKER_KEY)
public class NewCardSetupCompleteAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep9")
   final String cardSetupStep9 = "1";
}