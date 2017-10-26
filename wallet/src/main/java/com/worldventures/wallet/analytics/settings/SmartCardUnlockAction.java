package com.worldventures.wallet.analytics.settings;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:unlock card",
                trackers = AdobeTracker.TRACKER_KEY)
public class SmartCardUnlockAction extends WalletAnalyticsAction {

   @Attribute("cardunlock") String cardunlock = "1";
}
