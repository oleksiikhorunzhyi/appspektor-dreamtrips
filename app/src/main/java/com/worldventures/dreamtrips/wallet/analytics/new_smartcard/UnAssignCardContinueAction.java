package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:setup new smartcard:existing card detected:card connected:unassign card",
                trackers = AdobeTracker.TRACKER_KEY)
public class UnAssignCardContinueAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep3a")
   final String unAssignCardStep3a = "1";
}