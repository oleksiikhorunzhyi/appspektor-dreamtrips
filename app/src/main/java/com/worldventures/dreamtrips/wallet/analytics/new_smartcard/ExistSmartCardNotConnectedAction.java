package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card not connected",
                trackers = AdobeTracker.TRACKER_KEY)
public class ExistSmartCardNotConnectedAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep1b") final String unAssignCardStep1a = "1";
}
