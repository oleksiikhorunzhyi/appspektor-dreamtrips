package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card connected:unassign card:enter pin",
                trackers = AdobeTracker.TRACKER_KEY)
public class EnterPinUnAssignAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep4")
   final String unAssignCardStep4 = "1";
}