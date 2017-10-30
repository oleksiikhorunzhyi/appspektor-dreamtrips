package com.worldventures.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card now connected:unassign card",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class SmartCartWillNowBeAssignedAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep3b") final String unAssignCardStep3b = "1";
}
