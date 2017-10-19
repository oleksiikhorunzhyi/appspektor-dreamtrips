package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:unassign successful:get started",
                trackers = AdobeTracker.TRACKER_KEY)
public class UnAssignCardSuccessGetStartedAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep7")
   final String unAssignCardStep7 = "1";
}