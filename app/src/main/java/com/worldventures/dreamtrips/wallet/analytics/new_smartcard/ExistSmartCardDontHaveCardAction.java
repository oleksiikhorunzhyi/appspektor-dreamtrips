package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:setup new smartcard:existing card detected:card not connected:do not have card",
                trackers = AdobeTracker.TRACKER_KEY)
public class ExistSmartCardDontHaveCardAction extends WalletAnalyticsAction {

   @Attribute("unassigncardstep2c") final String unAssignCardStep2c = "1";
}
