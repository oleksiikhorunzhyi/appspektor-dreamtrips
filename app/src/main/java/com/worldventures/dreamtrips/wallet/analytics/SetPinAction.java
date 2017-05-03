package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;

@AnalyticsEvent(action = "wallet:setup:Step 6:Create your PIN",
                trackers = AdobeTracker.TRACKER_KEY)
public class SetPinAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstep6") final String cardSetupStep6 = "1";
}
