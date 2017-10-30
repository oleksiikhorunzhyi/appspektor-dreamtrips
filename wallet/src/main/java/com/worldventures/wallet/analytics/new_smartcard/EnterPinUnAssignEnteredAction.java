package com.worldventures.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card connected:unassign card:pin successfully entered",
                trackers = AdobeTracker.TRACKER_KEY)
public class EnterPinUnAssignEnteredAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep5") final String unAssignCardStep5 = "1";
}
