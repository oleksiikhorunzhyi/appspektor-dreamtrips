package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:reset your PIN",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class ResetPinAction extends WalletAnalyticsAction {

   @Attribute("resetpin1") String resetPin = "1";
}
