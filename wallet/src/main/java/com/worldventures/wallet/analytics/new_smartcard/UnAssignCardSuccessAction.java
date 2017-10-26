package com.worldventures.wallet.analytics.new_smartcard;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:unassign successful",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class UnAssignCardSuccessAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep6")
   final String unAssignCardStep6 = "1";
}