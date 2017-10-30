package com.worldventures.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card connected",
                trackers = AdobeTracker.TRACKER_KEY)
public class ExistSmartCardAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep1a") final String unAssignCardStep1a = "1";
}
