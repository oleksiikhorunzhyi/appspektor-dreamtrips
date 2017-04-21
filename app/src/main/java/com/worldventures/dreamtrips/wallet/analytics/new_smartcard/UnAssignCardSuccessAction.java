package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:unassign successful",
                trackers = AdobeTracker.TRACKER_KEY)
public class UnAssignCardSuccessAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep6")
   final String unAssignCardStep6 = "1";
}