package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 3:Card Successfully Connected",
                trackers = AdobeTracker.TRACKER_KEY)
public class CardConnectedAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep3") final String cardSetupStep3 = "1";
}
