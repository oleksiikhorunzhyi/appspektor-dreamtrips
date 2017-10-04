package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:lock card",
                trackers = AdobeTracker.TRACKER_KEY)
public class SmartCardLockAction extends WalletAnalyticsAction {

   @Attribute("cardlock") String cardlock = "1";
}
