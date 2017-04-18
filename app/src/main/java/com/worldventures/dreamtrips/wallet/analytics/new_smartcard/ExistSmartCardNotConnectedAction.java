package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:setup new smartcard:existing card detected:card not connected",
                trackers = AdobeTracker.TRACKER_KEY)
public class ExistSmartCardNotConnectedAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep1b")
   final String unAssignCardStep1a = "1";
}