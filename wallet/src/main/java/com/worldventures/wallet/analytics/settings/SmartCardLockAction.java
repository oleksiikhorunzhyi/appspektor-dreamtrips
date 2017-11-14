package com.worldventures.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:lock card",
                trackers = AdobeTracker.TRACKER_KEY)
public class SmartCardLockAction extends WalletAnalyticsAction {

   @Attribute("cardlock") String cardlock = "1";
}
