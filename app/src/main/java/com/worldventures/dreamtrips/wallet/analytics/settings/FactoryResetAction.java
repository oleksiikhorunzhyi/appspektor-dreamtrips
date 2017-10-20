package com.worldventures.dreamtrips.wallet.analytics.settings;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

@AnalyticsEvent(action = "wallet:settings:general:factory reset smart card",
                navigationState = true,
                trackers = AdobeTracker.TRACKER_KEY)
public class FactoryResetAction extends WalletAnalyticsAction {

   @Attribute("factoryresetcard") String factoryResetCard = "1";
}
