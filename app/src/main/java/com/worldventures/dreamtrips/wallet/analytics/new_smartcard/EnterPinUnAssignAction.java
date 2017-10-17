package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card connected:unassign card:enter pin",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class EnterPinUnAssignAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep4") final String unAssignCardStep4 = "1";
}
