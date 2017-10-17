package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:unlock card",
                trackers = AdobeTracker.TRACKER_KEY)
public class SmartCardUnlockAction extends WalletAnalyticsAction {

   @Attribute("cardunlock") String cardunlock = "1";
}
