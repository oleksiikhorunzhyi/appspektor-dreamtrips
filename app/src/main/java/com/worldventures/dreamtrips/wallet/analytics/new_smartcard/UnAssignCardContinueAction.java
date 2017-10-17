package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card connected:unassign card",
                trackers = AdobeTracker.TRACKER_KEY)
public class UnAssignCardContinueAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep3a") final String unAssignCardStep3a = "1";
}
