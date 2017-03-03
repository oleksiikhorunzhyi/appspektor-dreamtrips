package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:setup new smartcard:existing card detected:card not connected:power on smartcard",
                trackers = AdobeTracker.TRACKER_KEY)
public class ExistSmartCardHaveCardAction extends WalletAnalyticsAction {
   @Attribute("unassigncardstep2b")
   final String unAssignCardStep2b = "1";
}