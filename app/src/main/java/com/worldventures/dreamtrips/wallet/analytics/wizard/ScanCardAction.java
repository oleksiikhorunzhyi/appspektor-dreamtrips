package com.worldventures.dreamtrips.wallet.analytics.wizard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:setup:Step 1:Scan Card",
                trackers = AdobeTracker.TRACKER_KEY)
public class ScanCardAction extends WalletAnalyticsAction {

   @Attribute("cardsetupstart") final String cardSetupStart = "1";
}
