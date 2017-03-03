package com.worldventures.dreamtrips.wallet.analytics.new_smartcard;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:setup new smartcard:existing card detected:card not connected:do not have card:complete unassign",
                trackers = AdobeTracker.TRACKER_KEY)
public class ExistSmartCardDontHaveCardContinueAction extends WalletAnalyticsAction {
   @Attribute("unassigncardstep3c")
   final String unAssignCardStep3c = "1";
}